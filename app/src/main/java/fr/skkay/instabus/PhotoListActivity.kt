package fr.skkay.instabus

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.skkay.instabus.adapters.PhotoAdapter
import fr.skkay.instabus.contracts.PhotoContract
import fr.skkay.instabus.databaseHelper.PhotoDBHelper
import kotlinx.android.synthetic.main.activity_photo_list.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoListActivity : AppCompatActivity() {
    lateinit var database: SQLiteDatabase
    lateinit var station_id: String
    lateinit var currentImagePath: File

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_PREVIEW_CAPTURE = 2
    val REQUEST_PERMISSION_CAPTURE = 110

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)

        val dbHelper = PhotoDBHelper(this)
        database = dbHelper.writableDatabase
        station_id = intent.getStringExtra("id")!!

        val fab: FloatingActionButton = findViewById(R.id.fab_add_photo)
        fab.setOnClickListener { view ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CAPTURE)
            } else {
                takePicture()
            }
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) : Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val builder = AlertDialog.Builder(photo_recycler_view.context)
                builder.setCancelable(true)
                builder.setTitle("Attention")
                builder.setMessage("Confirmer la suppression ?")
                builder.setPositiveButton("Oui") { _, _ ->
                    run {
                        removePhotoFromDatabase(viewHolder.itemView.tag as Long)
                        refreshPhotoList()
                    }
                }
                builder.setNegativeButton("Non") { _, _ -> refreshPhotoList() }

                val dialog = builder.create()
                dialog.show()
            }
        }).attachToRecyclerView(photo_recycler_view)

        refreshPhotoList()
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CAPTURE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            previewPicture()
        }

        if (requestCode == REQUEST_PREVIEW_CAPTURE && resultCode == Activity.RESULT_OK) {
            val title = data?.getStringExtra("image_title")!!
            val path = data?.getStringExtra("image_path")!!
            addPhotoToDatabase(title, path, station_id)
            refreshPhotoList()
        }
        else if (requestCode == REQUEST_PREVIEW_CAPTURE && resultCode == Activity.RESULT_CANCELED) {
            removePhotoFromStorage(currentImagePath.toString())
        }
    }

    private fun getImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val pictureFile = File(path, "IMG_$timeStamp.jpg")

        currentImagePath = pictureFile.absoluteFile

        return pictureFile
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            var pictureFile: File? = null

            try {
                pictureFile = getImageFile()
            }
            catch (e: IOException) {
                e.printStackTrace()
            }

            if (pictureFile != null) {
                val imageUri: Uri = FileProvider.getUriForFile(this, "fr.skkay.instabus.provider", pictureFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
        catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Application non trouvée", Toast.LENGTH_SHORT).show()
        }
    }

    private fun previewPicture() {
        val previewPictureIntent = Intent(this, PhotoPreviewActivity::class.java)
        previewPictureIntent.putExtra("image_path", currentImagePath.toString())

        startActivityForResult(previewPictureIntent, REQUEST_PREVIEW_CAPTURE)
    }

    private fun addPhotoToDatabase(title: String, path: String, station_id: String) {
        val cv = ContentValues()
        cv.put(PhotoContract.PhotoEntry.COLUMN_TITLE, title)
        cv.put(PhotoContract.PhotoEntry.COLUMN_IMAGE, path)
        cv.put(PhotoContract.PhotoEntry.COLUMN_STATION_ID, station_id)

        database.insert(PhotoContract.PhotoEntry.TABLE_NAME, null, cv)
    }

    private fun removePhotoFromDatabase(id: Long) {
        val photoCursor = database.query(PhotoContract.PhotoEntry.TABLE_NAME, null, "${PhotoContract.PhotoEntry._ID} = ?", arrayOf(id.toString()), null, null, null)
        photoCursor.moveToFirst()

        val photoPath = photoCursor.getString(photoCursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_IMAGE))
        removePhotoFromStorage(photoPath)

        photoCursor.close()

        database.delete(PhotoContract.PhotoEntry.TABLE_NAME, "${PhotoContract.PhotoEntry._ID} = ?", arrayOf(id.toString()))
    }

    private fun removePhotoFromStorage(path: String) {
        try {
            File(path).delete()
        }
        catch (e: IOException) {
            Toast.makeText(this, "La photo n'a pas pu être supprimée des fichiers", Toast.LENGTH_LONG).show()
        }
    }

    private fun refreshPhotoList() {
        photo_recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = PhotoAdapter(getPhotosOfStation(station_id))
            setHasFixedSize(true)
        }
    }

    private fun getAllPhotos(): Cursor {
        return database.query(PhotoContract.PhotoEntry.TABLE_NAME, null, null, null, null, null, null)
    }

    private fun getPhotosOfStation(station_id: String): Cursor {
        return database.query(PhotoContract.PhotoEntry.TABLE_NAME, null, "${PhotoContract.PhotoEntry.COLUMN_STATION_ID} = ?", arrayOf(station_id), null, null, null)
    }
}

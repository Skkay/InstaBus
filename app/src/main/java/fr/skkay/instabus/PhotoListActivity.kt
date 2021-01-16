package fr.skkay.instabus

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.skkay.instabus.adapters.PhotoAdapter
import fr.skkay.instabus.contracts.PhotoContract
import fr.skkay.instabus.databaseHelper.PhotoDBHelper
import fr.skkay.instabus.dataclass.PhotoItem
import kotlinx.android.synthetic.main.activity_photo_list.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PhotoListActivity : AppCompatActivity() {
    lateinit var database: SQLiteDatabase
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)

        // Database
        val dbHelper = PhotoDBHelper(this)
        database = dbHelper.writableDatabase

        val intent = intent
        Log.i("intent_result", "id : ${intent.getStringExtra("id")} ; streetName : ${intent.getStringExtra("streetName")}")

        photo_recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = PhotoAdapter(getAllPhotos())
            setHasFixedSize(true)
        }

        addPhotoToDatabase()


        val fab: FloatingActionButton = findViewById(R.id.fab_add_photo)
        fab.setOnClickListener { view ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 110)
            } else {
                takePicture()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 110) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val image = data?.extras?.get("data") as Bitmap

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
            val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val pictureFile = File(path, "IMG_$timeStamp.jpg")
            val fos = FileOutputStream(pictureFile)

            Log.i("save_image", "path : $path")
            image.compress(CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        }
    }

    private fun takePicture()
    {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun addPhotoToDatabase()
    {
        val item = PhotoItem("title", 0, Calendar.getInstance().getTime(), "id") // will be camera result

        val cv = ContentValues()
        cv.put(PhotoContract.PhotoEntry.COLUMN_TITLE, item.title)
        cv.put(PhotoContract.PhotoEntry.COLUMN_IMAGE, item.image)
        cv.put(PhotoContract.PhotoEntry.COLUMN_STATION_ID, item.station_id)

        database.insert(PhotoContract.PhotoEntry.TABLE_NAME, null, cv)
    }

    private fun getAllPhotos(): Cursor {
        return database.query(PhotoContract.PhotoEntry.TABLE_NAME, null, null, null, null, null, null)
    }

    private fun getPhotosOfStation(station_id: String): Cursor {
        return database.query(PhotoContract.PhotoEntry.TABLE_NAME, null, "stationId = ?", arrayOf(station_id), null, null, null)
    }
}

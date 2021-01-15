package fr.skkay.instabus

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import fr.skkay.instabus.adapters.PhotoAdapter
import fr.skkay.instabus.contracts.PhotoContract
import fr.skkay.instabus.databaseHelper.PhotoDBHelper
import fr.skkay.instabus.dataclass.PhotoItem
import kotlinx.android.synthetic.main.activity_photo_list.*
import java.util.*
import kotlin.collections.ArrayList


class PhotoListActivity : AppCompatActivity() {
    lateinit var database: SQLiteDatabase

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
}

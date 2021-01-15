package fr.skkay.instabus

import android.content.ContentValues
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

        val dummyList = generateDummyList(100)

        photo_recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = PhotoAdapter(dummyList)
            setHasFixedSize(true)
        }

        addPhotoToDatabase()
    }

    private fun generateDummyList(size: Int): List<PhotoItem> {
        val list = ArrayList<PhotoItem>()

        for (i in 0 until size) {
            val item = PhotoItem("title $i", 0, Calendar.getInstance().getTime(), "id")
            list += item
        }

        return list
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
}

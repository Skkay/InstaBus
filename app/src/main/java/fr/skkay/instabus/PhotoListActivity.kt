package fr.skkay.instabus

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import fr.skkay.instabus.adapters.PhotoAdapter
import fr.skkay.instabus.dataclass.PhotoItem
import kotlinx.android.synthetic.main.activity_photo_list.*
import java.util.*
import kotlin.collections.ArrayList


class PhotoListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)

        val intent = intent
        Log.i("intent_result", "id : ${intent.getStringExtra("id")} ; streetName : ${intent.getStringExtra("streetName")}")

        val dummyList = generateDummyList(100)

        photo_recycler_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = PhotoAdapter(dummyList)
            setHasFixedSize(true)
        }
    }

    private fun generateDummyList(size: Int): List<PhotoItem> {
        val list = ArrayList<PhotoItem>()

        for (i in 0 until size) {
            val item = PhotoItem("title $i", 0, Calendar.getInstance().getTime(), "id")
            list += item
        }

        return list
    }
}

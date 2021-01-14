package fr.skkay.instabus

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class PhotoListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)

        val intent = intent
        Log.i("intent_result", "id : ${intent.getStringExtra("id")} ; streetName : ${intent.getStringExtra("streetName")}")
    }
}

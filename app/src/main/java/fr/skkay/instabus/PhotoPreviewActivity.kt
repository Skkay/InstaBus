package fr.skkay.instabus

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class PhotoPreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_preview)

        val imageView = findViewById<ImageView>(R.id.preview_image)
        val editText = findViewById<EditText>(R.id.edit_text_image_title)
        val button = findViewById<Button>(R.id.button_set_title)

        val imagePath = intent.getStringExtra("image_path")

        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)

        button.setOnClickListener { view ->
            val title = editText.text.toString()
            if (!title.isNullOrBlank()) {
                val resultIntent = Intent()
                resultIntent.putExtra("image_title", title)
                resultIntent.putExtra("image_path", imagePath)

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            else {
                Toast.makeText(this, "Nom incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package fr.skkay.instabus.adapters

import android.database.Cursor
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.skkay.instabus.R
import fr.skkay.instabus.contracts.PhotoContract
import kotlinx.android.synthetic.main.photo_item.view.*
import java.io.File

class PhotoAdapter(private val cursor: Cursor) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (!cursor.moveToPosition(position)) return

        val imagePath = cursor.getString(cursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_IMAGE))
        if (File(imagePath).exists()) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath))
        }
        else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_cancel_24)
        }
        holder.textView1.text = cursor.getString(cursor.getColumnIndex((PhotoContract.PhotoEntry.COLUMN_TITLE)))
        holder.textView2.text = cursor.getString(cursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_TIMESTAMP))
        holder.itemView.tag = cursor.getLong(cursor.getColumnIndex(PhotoContract.PhotoEntry._ID))
    }

    override fun getItemCount(): Int = cursor.count

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.photo_text_view_1
        val textView2: TextView = itemView.photo_text_view_2
    }
}

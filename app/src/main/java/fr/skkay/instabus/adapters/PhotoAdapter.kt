package fr.skkay.instabus.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.skkay.instabus.R
import fr.skkay.instabus.contracts.PhotoContract
import kotlinx.android.synthetic.main.photo_item.view.*

class PhotoAdapter(private val cursor: Cursor) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (!cursor.moveToPosition(position)) return

        holder.imageView.setImageResource(cursor.getInt(cursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_IMAGE)))
        holder.textView1.text = cursor.getString(cursor.getColumnIndex((PhotoContract.PhotoEntry.COLUMN_TITLE)))
        holder.textView2.text = cursor.getString(cursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_TIMESTAMP))
    }

    override fun getItemCount(): Int = cursor.count

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.photo_text_view_1
        val textView2: TextView = itemView.photo_text_view_2
    }
}

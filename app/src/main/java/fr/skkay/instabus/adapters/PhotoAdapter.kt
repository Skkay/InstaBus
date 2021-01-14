package fr.skkay.instabus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.skkay.instabus.R
import fr.skkay.instabus.dataclass.PhotoItem
import kotlinx.android.synthetic.main.photo_item.view.*
import java.text.DateFormat

class PhotoAdapter(private val list: List<PhotoItem>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = list[position]

        holder.imageView.setImageResource(currentItem.image)
        holder.textView1.text = currentItem.title
        holder.textView2.text = currentItem.datetime.toString()
    }

    override fun getItemCount(): Int = list.size

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.photo_text_view_1
        val textView2: TextView = itemView.photo_text_view_2
    }
}

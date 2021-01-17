package fr.skkay.instabus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.skkay.instabus.R
import fr.skkay.instabus.dataclass.StationItem

class StationAdapter(private val list: List<StationItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.station_item, parent, false)
        return StationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val currentItem = list[position]

        holder.stationName.text = currentItem.streetName
    }

    override fun getItemCount(): Int = list.size

    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val stationName: TextView = itemView.findViewById(R.id.station_text_view)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}

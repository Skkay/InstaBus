package fr.skkay.instabus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.skkay.instabus.R
import fr.skkay.instabus.dataclass.StationItem

class StationAdapter(private val list: List<StationItem>) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.station_item, parent, false)
        return StationViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val currentItem = list[position]

        holder.stationName.text = currentItem.streetName
    }

    override fun getItemCount(): Int = list.size

    class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stationName: TextView = itemView.findViewById(R.id.station_text_view)
    }
}
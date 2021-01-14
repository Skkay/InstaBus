package fr.skkay.instabus.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import fr.skkay.instabus.DetailStation
import fr.skkay.instabus.R
import fr.skkay.instabus.adapters.StationAdapter
import fr.skkay.instabus.dataclass.StationItem
import fr.skkay.instabus.services.StationService
import kotlinx.android.synthetic.main.fragment_list.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class ListFragment : Fragment(), StationAdapter.OnItemClickListener {

    private val baseURL: String = "http://barcelonaapi.marcpous.com/"
    private var stationList: List<StationItem> = emptyList()

    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Loading bus stations from API
        val res = loadBusStationsFromAPI()
        stationList = parseJSONBusStation(res)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = StationAdapter(stationList, this@ListFragment)
        }
    }

    override fun onItemClick(position: Int) {
        val clickedStation = stationList[position]
        Log.i("item_click", "item : ${clickedStation.streetName}")

        val intent = Intent(this.context, DetailStation::class.java).apply {
            putExtra("id", clickedStation.id)
            putExtra("streetName", clickedStation.streetName)
            putExtra("city", clickedStation.city)
            putExtra("utm_x", clickedStation.utm_x)
            putExtra("utm_y", clickedStation.utm_y)
            putExtra("lat", clickedStation.lat)
            putExtra("lon", clickedStation.lon)
            putExtra("furniture", clickedStation.furniture)
            putExtra("buses", clickedStation.buses)
        }
        startActivity(intent)
    }

    private fun loadBusStationsFromAPI(): String {
        var json_response = ""
        val thread = Thread(Runnable {
            try {
                val retrofit = Retrofit.Builder().baseUrl(this.baseURL).addConverterFactory(MoshiConverterFactory.create()).build()
                val service = retrofit.create(StationService::class.java)
                val response = service.getStations().execute()
                val stations = response.body()?.string()

                if (stations != null) {
                    json_response = stations
                }
            }
            catch (e: Exception) {
                Log.i("json_res", "Catch error :" + e.localizedMessage)
            }
        })
        thread.start()
        thread.join()
        return json_response
    }

    private fun parseJSONBusStation(json: String): List<StationItem> {
        val root: JSONObject = JSONObject(json)
        val data: JSONObject = root.getJSONObject("data")
        val stations: JSONArray = data.getJSONArray("tmbs")

        val list = ArrayList<StationItem>()
        for (i in 0 until stations.length()) {
            val stationObject: JSONObject = stations.getJSONObject(i)
            val stationItem = StationItem(
                stationObject.getString("id"),
                stationObject.getString("street_name"),
                stationObject.getString("city"),
                stationObject.getString("utm_x"),
                stationObject.getString("utm_y"),
                stationObject.getString("lat"),
                stationObject.getString("lon"),
                stationObject.getString("furniture"),
                stationObject.getString("buses")
            )
            list += stationItem
        }
        return list
    }
}

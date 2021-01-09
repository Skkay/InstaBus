package fr.skkay.instabus.ui.main.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import fr.skkay.instabus.R
import fr.skkay.instabus.adapters.StationAdapter
import fr.skkay.instabus.dataclass.StationItem
import fr.skkay.instabus.services.StationService
import kotlinx.android.synthetic.main.fragment_list.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class ListFragment : Fragment() {

    private val baseURL: String = "http://barcelonaapi.marcpous.com/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = generateDummyList(100)

        // Loading bus stations from API
        val res = loadBusStationsFromAPI()
        if ( res.isEmpty() ) {
            Log.i("json_res", res)
        }
        else {
            Log.i("json_res", "json response empty")
        }
        val stationList = parseJSONBusStation(res)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = StationAdapter(stationList)
        }
    }

    companion object {
        /**
         * @return A new instance of fragment ListFragment.
         */
        @JvmStatic
        fun newInstance() = ListFragment()
    }

    private fun generateDummyList(size: Int): List<StationItem> {
        val list = ArrayList<StationItem>()

        for (i in 0 until size) {
            val item = StationItem("Item $i")
            list += item
        }

        return list
    }

    private fun loadBusStationsFromAPI(): String {
        var test_response = ""
        val thread = Thread(Runnable {
            try {
                val retrofit = Retrofit.Builder().baseUrl(this.baseURL).addConverterFactory(MoshiConverterFactory.create()).build()
                val service = retrofit.create(StationService::class.java)
                val response = service.getStations().execute()
                val stations = response.body()?.string()

                if (stations != null) {
                    test_response = stations
                    Log.i("json_res", stations)
                }
            }
            catch (e: Exception) {
                Log.i("json_res", "Catch error :" + e.localizedMessage)
            }
        })
        thread.start()
        thread.join()
        return test_response
    }

    private fun parseJSONBusStation(json: String): List<StationItem> {
        val root: JSONObject = JSONObject(json)
        val data: JSONObject = root.getJSONObject("data")
        val stations: JSONArray = data.getJSONArray("tmbs")

        val list = ArrayList<StationItem>()
        for (i in 0 until stations.length()) {
            val stationObject: JSONObject = stations.getJSONObject(i)
            val stationItem = StationItem(stationObject.getString("street_name"))
            list += stationItem
        }
        return list
    }
}

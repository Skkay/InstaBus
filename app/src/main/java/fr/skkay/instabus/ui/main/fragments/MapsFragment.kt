package fr.skkay.instabus.ui.main.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.skkay.instabus.R
import fr.skkay.instabus.dataclass.StationItem
import fr.skkay.instabus.services.StationService
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MapsFragment : Fragment() {

    private val baseURL: String = "http://barcelonaapi.marcpous.com/"

    companion object {
        @JvmStatic
        fun newInstance() = MapsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        // Loading bus stations from API
        val res = loadBusStationsFromAPI()
        val stationList = parseJSONBusStation(res)

        // Setting Google Maps options
        val callback = OnMapReadyCallback { googleMap ->
            val defaultPlacement = LatLng(41.3750532, 2.1490632)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPlacement, 18f))

            for (station in stationList)  {
                googleMap.addMarker(MarkerOptions().position(LatLng(station.lat.toDouble(), station.lon.toDouble())).title(station.streetName))
            }
        }
        mapFragment?.getMapAsync(callback)
    }

    private fun loadBusStationsFromAPI(): String {
        var json_response = ""
        val thread = Thread(Runnable {
            try {
                val retrofit = Retrofit.Builder().baseUrl(this.baseURL).addConverterFactory(
                    MoshiConverterFactory.create()).build()
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

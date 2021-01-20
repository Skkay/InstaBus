package fr.skkay.instabus.ui.main.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import fr.skkay.instabus.R
import fr.skkay.instabus.dataclass.StationItem
import fr.skkay.instabus.services.StationService
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MapsFragment : Fragment() {
    private val baseURL: String = "http://barcelonaapi.marcpous.com/"
    private val REQUEST_CODE = 101

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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

        context?.let {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(it)
        }

        // Setting Google Maps options
        val callback = OnMapReadyCallback { googleMap ->
            if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
            else {
                val task: Task<Location> = fusedLocationProviderClient.getLastLocation()
                task.addOnSuccessListener { location ->
                    var loc = LatLng(41.3750532, 2.1490632) // Default localization
                    if (location == null) {
                        Toast.makeText(context, getString(R.string.gps_position_error), Toast.LENGTH_LONG).show();
                    }
                    else {
                        loc = LatLng(location.latitude, location.longitude)
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))
                }
                task.addOnFailureListener {
                    Toast.makeText(context, getString(R.string.gps_position_error), Toast.LENGTH_LONG).show();
                    val loc = LatLng(41.3750532, 2.1490632) // Default localization
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))
                }
            }

            googleMap.setMaxZoomPreference(18.5f)
            googleMap.setMinZoomPreference(12f)

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

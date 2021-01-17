package fr.skkay.instabus.services

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface StationService {
    @GET("bus/stations.json")
    fun getStations(): Call<ResponseBody>
}

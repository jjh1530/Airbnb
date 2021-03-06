package com.jjhadr.airbnb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private val mapView: MapView by lazy {
        findViewById(R.id.mapView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView.onCreate(savedInstanceState)

        //onMapReady callback
        mapView.getMapAsync(this)

    }
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.124219,126.869231,))
        naverMap.moveCamera(cameraUpdate)

        // νμμΉ
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true

        locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

//        val marker = Marker()
//        marker.position = LatLng(35.124219,126.869231)
//        marker.map = naverMap
//        marker.icon = MarkerIcons.BLUE

        getHouseListFromAPI()
    }

    private fun getHouseListFromAPI() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(HouseService::class.java).also {
            it.getHouseList()
                .enqueue(object : Callback<HouseDto>{
                    override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                        if (response.isSuccessful.not()) {
                            //μ€ν¨ μ²λ¦¬μ λν κ΅¬ν
                            return
                        }
                        response.body()?.let { dto ->
                            dto.items.forEach {  house ->
                                val marker = Marker()
                                marker.position = LatLng(house.lat,house.lng)
                                marker.map = naverMap
                                marker.tag = house.id
                                marker.icon = MarkerIcons.BLUE
                            }
                        }
                    }

                    override fun onFailure(call: Call<HouseDto>, t: Throwable) {
                        //μ€ν¨ μ²λ¦¬
//
                    }
                })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE =1000
    }
}
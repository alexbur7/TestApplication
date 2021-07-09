package com.example.testapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.network.NetworkService
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity:AppCompatActivity(),OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        var number = 0
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        NetworkService.getGeoPositionApi().createPost()
            .flatMap {
                Flowable.just(it.featureModel[0].geometry.multiPolygon)
            }
            .flatMapIterable { it }
            .flatMapIterable { it }
            .flatMapIterable { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { geoModel->
                Log.d("tut_model", "$number $geoModel")
                number++
            },{
                Log.e("tut_error",it.message.toString())
            })
    }
    override fun onMapReady(googleMap: GoogleMap) {
    }
}
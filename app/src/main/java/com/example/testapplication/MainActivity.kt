package com.example.testapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.network.NetworkService
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity:AppCompatActivity(),OnMapReadyCallback {

    private companion object{
        const val BASE_URL = "https://waadsu.com/api/russia.geo.json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
         Observable.just(BASE_URL).map {
            val inputStream = URL(it).openStream()
             return@map JsonReader(InputStreamReader(inputStream, "UTF-8"))
            }
            .flatMap { jsonReader ->
                return@flatMap Observable.create<LatLng> { emitter ->
                    with(jsonReader) {
                        while (hasNext()) {
                            if (peek() == JsonToken.BEGIN_OBJECT) {
                                beginObject()
                            }
                            if (nextName() == "features") {
                                beginArray()
                                while (hasNext()) {
                                    if (peek() == JsonToken.BEGIN_OBJECT) {
                                        beginObject()
                                    }
                                    if (nextName() == "geometry") {
                                        beginObject()
                                        while (hasNext()) {
                                            if (nextName() == "coordinates") {
                                                beginArray()
                                                while (hasNext()) {
                                                    beginArray()
                                                    beginArray()
                                                    while (hasNext()) {
                                                        beginArray()
                                                        val latLng =
                                                            LatLng(nextDouble(), nextDouble())
                                                        emitter.onNext(latLng)
                                                        endArray()
                                                    }
                                                    endArray()
                                                    endArray()
                                                }
                                                endArray()
                                            } else {
                                                skipValue()
                                            }
                                        }
                                        endObject()
                                    } else {
                                        skipValue()
                                    }
                                }
                                endObject()
                                endArray()
                            } else {
                                skipValue()
                            }
                        }
                        endObject()
                        close()
                        emitter.onComplete()
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("tut_onNext", "$it")
            },{
            Log.e("tut_error",it.message.toString())
        },{
                Log.d("tut_complete", "onComplete")
            })

    }
    override fun onMapReady(googleMap: GoogleMap) {
    }
}
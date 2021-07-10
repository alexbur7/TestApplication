package com.example.testapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity:AppCompatActivity(),OnMapReadyCallback {

    private var progressBar:ProgressBar? = null
    private var restartButton:Button? = null
    private var googleMap:GoogleMap? = null
    private val observable by lazy { DownloadCoordinatesManager().createObservable() }
    private var disposable:Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        progressBar = findViewById(R.id.progress_circular)
        restartButton = findViewById(R.id.restart_download)
        restartButton?.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            it.visibility = View.GONE
            downloadData()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        downloadData()
    }

    private fun downloadData() {
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(createObserver())
    }

    private fun createObserver(): Observer<List<LatLng>> =
        object : Observer<List<LatLng>>{
            override fun onNext(data: List<LatLng>) {
                val polylineOptions = PolylineOptions().apply {
                    color(Color.RED)
                    addAll(data)
                }
                googleMap?.addPolyline(polylineOptions)
            }

            override fun onError(e: Throwable) {
                progressBar?.visibility = View.GONE
                restartButton?.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity,this@MainActivity.
                            getString(R.string.no_internet),Toast.LENGTH_SHORT ).show()
                Log.e("tut_error",e.message.toString())
            }

            override fun onComplete() {
                progressBar?.visibility = View.GONE
            }

            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

        }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
        googleMap = null
    }

}
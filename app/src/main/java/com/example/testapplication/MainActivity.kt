package com.example.testapplication

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class MainActivity:AppCompatActivity(),OnMapReadyCallback {

    private companion object{
        const val MULTI_POLYLINE_KEY = "multi_polyline_key"
    }
    private var progressBar:ProgressBar? = null
    private var restartButton:Button? = null
    private var googleMap:GoogleMap? = null
    private val observable by lazy { DownloadCoordinatesManager().createObservable() }
    private var disposable:Disposable? = null
    private val multiPolyline = mutableListOf<PolylineModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState != null){
            savedInstanceState.getParcelableArrayList<PolylineModel>(MULTI_POLYLINE_KEY)
                ?.let { multiPolyline.addAll(it.toMutableList()) }
        }
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
        if (multiPolyline.size == 0) {
            downloadData()
        }
        else{
            progressBar?.visibility = View.GONE
            multiPolyline.forEach { polylineModel ->
                drawMap(polylineModel)
            }
        }
    }

    private fun downloadData() {
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(createObserver())
    }

    private fun createObserver(): Observer<PolylineModel> =
        object : Observer<PolylineModel>{
            override fun onNext(data: PolylineModel) {
                drawMap(data)
                multiPolyline.add(data)
            }

            override fun onError(e: Throwable) {
                progressBar?.visibility = View.GONE
                restartButton?.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity,this@MainActivity.
                            getString(R.string.no_internet),Toast.LENGTH_SHORT ).show()
            }

            override fun onComplete() {
                progressBar?.visibility = View.GONE
            }

            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

        }

    private fun drawMap(data: PolylineModel) {
        val polylineOptions = PolylineOptions().apply {
            color(Color.RED)
            addAll(data.coordinates)
        }
        googleMap?.addPolyline(polylineOptions)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(MULTI_POLYLINE_KEY , multiPolyline
                as ArrayList<out Parcelable>)
    }
    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
        googleMap = null
    }

}
package com.example.testapplication.network

import com.example.testapplication.GeoModel
import io.reactivex.Flowable
import retrofit2.http.GET

interface GeoPositionApi {

    @GET("api/russia.geo.json")
    fun createPost():Flowable<GeoModel>
}
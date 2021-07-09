package com.example.testapplication.network

import com.example.testapplication.GeoModel
import io.reactivex.Flowable
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.http.GET

interface GeoPositionApi {

    @GET("api/russia.geo.json")
    fun createPost():Flowable<GeoModel>
}
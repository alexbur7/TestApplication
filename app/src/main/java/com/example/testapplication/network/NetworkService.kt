package com.example.testapplication.network

import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val BASE_URL = "https://waadsu.com/"
    private val httpClient = OkHttpClient()
    private val gson = GsonBuilder().apply {
    }.create()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .client(httpClient)
        .baseUrl(BASE_URL)
        .build()

    fun getGeoPositionApi(): GeoPositionApi {
        return retrofit.create(GeoPositionApi::class.java)
    }
}
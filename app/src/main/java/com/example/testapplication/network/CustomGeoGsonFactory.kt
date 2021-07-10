package com.example.testapplication.network

import com.example.testapplication.GeoModel
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


/*class CustomGeoGsonFactory:JsonDeserializer<List<Double>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Double> {
        val gson = Gson()
        val geoModel = gson.fromJson(json,GeoModel::class.java)

    }
}*/
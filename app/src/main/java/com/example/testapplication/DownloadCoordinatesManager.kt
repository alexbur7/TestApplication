package com.example.testapplication

import com.google.android.gms.maps.model.LatLng
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import io.reactivex.Observable
import java.io.InputStreamReader
import java.net.URL

class DownloadCoordinatesManager {

    private companion object {
        const val BASE_URL = "https://waadsu.com/api/russia.geo.json"
    }

    fun createObservable(): Observable<PolylineModel> = Observable.just(BASE_URL).map {
        val inputStream = URL(it).openStream()
        return@map JsonReader(InputStreamReader(inputStream, "UTF-8"))
    }
        .flatMap { jsonReader ->
            return@flatMap Observable.create<PolylineModel> { emitter ->
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
                                                val polyline = mutableListOf<LatLng>()
                                                while (hasNext()) {
                                                    beginArray()
                                                    val latitude = nextDouble()
                                                    val longitude = nextDouble()
                                                    val latLng =
                                                        LatLng(longitude, latitude)
                                                    polyline.add(latLng)
                                                    endArray()
                                                }
                                                endArray()
                                                endArray()
                                                emitter.onNext(PolylineModel(polyline))
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
}
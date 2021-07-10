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

    //функция для создания обсервабла, которая будет имитеть polyline в onNext
    fun createObservable(): Observable<PolylineModel> = Observable.just(BASE_URL).map {
        //получения потока данных по ссылке
        val inputStream = URL(it).openStream()
        return@map JsonReader(InputStreamReader(inputStream, "UTF-8"))
    }
        .flatMap { jsonReader -> //десирелизация данных из стрима динамически
            return@flatMap Observable.create<PolylineModel> { emitter ->
                with(jsonReader) {
                    //открываем самый первый объект json
                    while (hasNext()) {
                        if (peek() == JsonToken.BEGIN_OBJECT) {
                            beginObject()
                        }
                        //открываем массив с названием features, другие элементы пропускаем
                        if (nextName() == "features") {
                            beginArray()
                            while (hasNext()) {
                                //открываем объект feature
                                if (peek() == JsonToken.BEGIN_OBJECT) {
                                    beginObject()
                                }
                                //открываем объект c названием geometry, другие элементы пропускаем
                                if (nextName() == "geometry") {
                                    beginObject()
                                    while (hasNext()) {
                                        //открываем массив координат, другие элементы пропускаем
                                        if (nextName() == "coordinates") {
                                            beginArray()
                                            while (hasNext()) {
                                                beginArray()
                                                beginArray()
                                                //создаем список координат для каждого слоя
                                                val polyline = mutableListOf<LatLng>()
                                                while (hasNext()) {
                                                    beginArray()
                                                    val latitude = nextDouble()
                                                    val longitude = nextDouble()
                                                    //создаем координату, используя долготу и широту
                                                    val latLng =
                                                        LatLng(longitude, latitude)
                                                    polyline.add(latLng)
                                                    endArray()
                                                }
                                                endArray()
                                                endArray()
                                                //эмитим список координат в onNext
                                                emitter.onNext(PolylineModel(polyline))
                                            }
                                            endArray()
                                        } else {
                                            //пропуск неиспользованных данных
                                            skipValue()
                                        }
                                    }
                                    endObject()
                                } else {
                                    //пропуск неиспользованных данных
                                    skipValue()
                                }
                            }
                            endObject()
                            endArray()
                        } else {
                            //пропуск неиспользованных данных
                            skipValue()
                        }
                    }
                    endObject()
                    close()
                    //после отправки всех списков координат вызываем onComplete
                    emitter.onComplete()
                }
            }
        }
}
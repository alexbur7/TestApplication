package com.example.testapplication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeoModel(
    @SerializedName("type")
    val type:String,
    @SerializedName("features")
    val featureModel: Array<FeatureModel>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeoModel

        if (type != other.type) return false
        if (!featureModel.contentEquals(other.featureModel)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + featureModel.contentHashCode()
        return result
    }
}


data class FeatureModel(
    @SerializedName("geometry")
    val geometry: GeometryModel
)
data class GeometryModel(
    @SerializedName("coordinates")
    val multiPolygon: List<List<List<List<Double>>>>
    )

data class LatLong(val lat:Double, val long:Double)
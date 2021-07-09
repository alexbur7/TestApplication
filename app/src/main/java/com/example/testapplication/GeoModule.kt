package com.example.testapplication

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

data class GeoModel(
    @SerializedName("type")
    val type: String,
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
    @SerializedName("type")
    val type:String,
    @SerializedName("geometry")
    val geometry: GeometryModel
)
data class GeometryModel(
    @SerializedName("type")
    val type:String,
    @SerializedName("coordinates")
    val multiPolygon: List<List<List<List<Double>>>>
    )

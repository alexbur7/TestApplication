package com.example.testapplication

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class PolylineModel(val coordinates: List<LatLng>) : Parcelable
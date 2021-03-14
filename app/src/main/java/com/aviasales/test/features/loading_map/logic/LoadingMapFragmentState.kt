package com.aviasales.test.features.loading_map.logic

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoadingMapFragmentState(
    val fromPoint: LatLng,
    val toPoint: LatLng,
    val fromName: String,
    val toName: String
) : Parcelable
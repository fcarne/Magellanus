package com.unibg.magellanus.app.itinerary.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class POI(
    var name: String?,
    val latitude: Double,
    val longitude: Double,
    var address: Address?
): Parcelable

@Parcelize
data class Address(
    val country: String,
    val countryCode: String,
    val city: String?,
    val postcode: String?,
    val county: String?,
    val street: String?,
    val state: String?
): Parcelable

package com.unibg.magellanus.app.itinerary.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class POI(
    var name: String? = null,
    val lat: Double,
    val lon: Double,
    @Transient var address: Address? = null
) {
    override fun hashCode() = Objects.hash(lat, lon)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as POI

        if (lat != other.lat) return false
        if (lon != other.lon) return false

        return true
    }
}

data class Address(
    val country: String,
    @SerializedName(value = "countrycode")
    val countryCode: String,
    val city: String?,
    val postcode: String?,
    val county: String?,
    val street: String?,
    val state: String?
) {
    val formatted
        get() = "$street, $county, $city $postcode, $state, $country $countryCode"
}
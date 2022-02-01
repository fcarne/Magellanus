package com.unibg.magellanus.app.route.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RoutedPOI(
    var name: String? = null,
    val coordinates: Coordinates,
    var distance: Double = 0.0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoutedPOI

        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates.hashCode()
    }
}

@Parcelize
data class Coordinates(
    val lat: Double,
    val lon: Double
): Parcelable
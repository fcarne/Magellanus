package com.unibg.magellanus.app.itinerary.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.time.LocalDate

@Parcelize
data class Itinerary(
    val id: String? = null,
    val owner: String? = null,
    val name: String,
    val date: LocalDate? = null,
    val poiSet: @RawValue MutableSet<POI> = HashSet()
): Parcelable
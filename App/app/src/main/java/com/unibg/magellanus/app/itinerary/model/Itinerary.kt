package com.unibg.magellanus.app.itinerary.model

import java.time.LocalDate

data class Itinerary(
    val id: String? = null,
    val owner: String? = null,
    val name: String,
    val completionDate: LocalDate? = null,
    val poiSet: MutableSet<POI> = HashSet()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Itinerary

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result
    }
}
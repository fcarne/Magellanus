package com.unibg.magellanus.app.route.model

import java.util.*

data class Route(
    val id: String? = null,
    val owner: String? = null,
    val itineraryId: String,
    var route: MutableList<RoutedPOI> = LinkedList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (id != other.id) return false
        if (itineraryId != other.itineraryId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + itineraryId.hashCode()
        return result
    }
}
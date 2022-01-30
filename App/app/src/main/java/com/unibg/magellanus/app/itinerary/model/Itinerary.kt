package com.unibg.magellanus.app.itinerary.model

import java.time.LocalDate

data class Itinerary(
    val id: String? = null,
    val owner: String? = null,
    val name: String,
    val completionDate: LocalDate? = null,
    val poiSet: MutableSet<POI> = HashSet()
)
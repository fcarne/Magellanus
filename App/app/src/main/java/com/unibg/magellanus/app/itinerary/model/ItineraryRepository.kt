package com.unibg.magellanus.app.itinerary.model

interface ItineraryRepository {
    suspend fun get(id: String): Itinerary?
    suspend fun create(itinerary: Itinerary): Itinerary?
    suspend fun update(itinerary: Itinerary)
    suspend fun delete(itinerary: Itinerary)
    suspend fun getAll(completed: Boolean?): Iterable<Itinerary>
    suspend fun getInfo(poi: POI): POI
    suspend fun search(query: String): Iterable<POI>
}
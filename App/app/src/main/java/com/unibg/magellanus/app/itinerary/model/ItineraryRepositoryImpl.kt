package com.unibg.magellanus.app.itinerary.model

import com.unibg.magellanus.app.itinerary.model.network.GeocodingAPI
import com.unibg.magellanus.app.itinerary.model.network.ItineraryAPI

class ItineraryRepositoryImpl(
    private val itineraryAPI: ItineraryAPI,
    private val geocodingAPI: GeocodingAPI
) : ItineraryRepository {
    override suspend fun get(id: String): Itinerary? {
        return itineraryAPI.get(id)
    }

    override suspend fun create(itinerary: Itinerary): Itinerary? {
        return itineraryAPI.create(itinerary)
    }

    override suspend fun update(itinerary: Itinerary) {
        itineraryAPI.updateMine(itinerary.id!!, itinerary)
    }

    override suspend fun delete(itinerary: Itinerary) {
        itineraryAPI.deleteMine(itinerary.id!!)
    }

    override suspend fun getAll(completed: Boolean?): Iterable<Itinerary> {
        return itineraryAPI.findMine(completed)
    }

    override suspend fun getInfo(poi: POI): POI {
        return geocodingAPI.reverseSearch(poi.latitude, poi.longitude)
    }

    override suspend fun search(query: String): Iterable<POI> {
        return geocodingAPI.search(query, 5)
    }
}
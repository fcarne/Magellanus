package com.unibg.magellanus.app.itinerary.model

import com.unibg.magellanus.app.itinerary.model.network.GeocodingAPI
import com.unibg.magellanus.app.itinerary.model.network.ItineraryAPI

class ItineraryRepositoryImpl(
    private val itineraryAPI: ItineraryAPI,
    private val geocodingAPI: GeocodingAPI
) : ItineraryRepository {
    override suspend fun get(id: String): Itinerary? {
        val response = itineraryAPI.get(id)
        return if(response.isSuccessful)
            response.body()
        else null
    }

    override suspend fun getAllInfo(id: String): Itinerary? {
        return get(id)?.let {
            val set = it.poiSet.map { poi -> getInfo(poi) }.toHashSet()
            it.copy(poiSet = set)
        }
    }

    override suspend fun create(itinerary: Itinerary): Itinerary? {
        val response = itineraryAPI.create(itinerary)
        return if(response.isSuccessful)
            response.body()
        else null
    }

    override suspend fun update(itinerary: Itinerary) {
        itineraryAPI.updateMine(itinerary.id!!, itinerary)
    }

    override suspend fun delete(itinerary: Itinerary) {
        itineraryAPI.deleteMine(itinerary.id!!)
    }

    override suspend fun getAll(completed: Boolean?): List<Itinerary> {
        return itineraryAPI.findMine(completed)
    }

    override suspend fun getInfo(poi: POI): POI {
        return geocodingAPI.reverseSearch(poi.lat, poi.lon).features[0]
    }

    override suspend fun getInfo(lat: Double, lon: Double): POI? {
        return geocodingAPI.reverseSearch(lat, lon).features.firstOrNull()
    }

    override suspend fun search(query: String): List<POI> {
        return geocodingAPI.search(query, 5).features
    }
}
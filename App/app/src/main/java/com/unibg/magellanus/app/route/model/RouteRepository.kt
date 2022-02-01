package com.unibg.magellanus.app.route.model

interface RouteRepository {
    suspend fun get(id: String): Route?
    suspend fun getByItinerary(id: String): Route?
    suspend fun create(route: Route): Route?
    suspend fun update(route: Route)
    suspend fun delete(route: Route)
    suspend fun autoGenerate(route: Route): Route?
    suspend fun getDistance(list: List<RoutedPOI>): List<List<Double>>
}
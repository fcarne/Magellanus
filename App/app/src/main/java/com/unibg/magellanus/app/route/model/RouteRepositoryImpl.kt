package com.unibg.magellanus.app.route.model

import com.unibg.magellanus.app.route.model.network.MatrixAPI
import com.unibg.magellanus.app.route.model.network.RouteAPI

class RouteRepositoryImpl(private val routeAPI: RouteAPI, private val matrixAPI: MatrixAPI) :
    RouteRepository {
    override suspend fun get(id: String): Route? {
        val response = routeAPI.get(id)
        return if (response.isSuccessful)
            response.body()
        else null
    }

    override suspend fun getByItinerary(id: String): Route? {
        val response = routeAPI.getByItinerary(id)
        return if (response.isSuccessful)
            response.body()
        else null
    }

    override suspend fun create(route: Route): Route? {
        val response = routeAPI.create(route)
        return if (response.isSuccessful)
            response.body()
        else null
    }

    override suspend fun update(route: Route) {
        routeAPI.updateMine(route.id!!, route)
    }

    override suspend fun delete(route: Route) {
        routeAPI.deleteMine(route.id!!)
    }

    override suspend fun autoGenerate(route: Route): Route? {
        val response = routeAPI.autoGenerate(route)
        return if (response.isSuccessful)
            response.body()
        else null
    }

    override suspend fun getDistance(list: List<RoutedPOI>): List<List<Double>> {
        // crea l'url a cui far richiesta per matrixAPI
        val coordinates =
            list.joinToString(";") { poi -> poi.coordinates.let { "${it.lon},${it.lat}" } }
        return matrixAPI.getDistances(coordinates).distances
    }

}
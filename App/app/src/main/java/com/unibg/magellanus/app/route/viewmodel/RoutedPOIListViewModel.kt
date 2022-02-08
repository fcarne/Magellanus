package com.unibg.magellanus.app.route.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.route.model.Route
import com.unibg.magellanus.app.route.model.RouteRepository
import com.unibg.magellanus.app.route.model.RoutedPOI
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import kotlin.math.*

class RoutedPOIListViewModel(private val repository: RouteRepository) :
    ViewModel() {

    private val _currentRoute: MutableLiveData<Route> = MutableLiveData()
    val poiRoute: LiveData<List<RoutedPOI>> = _currentRoute.map { it.route }

    private val _removedIndex: MutableLiveData<Int> = MutableLiveData()
    val removedIndex: LiveData<Int>
        get() = _removedIndex

    private val _movedFromIndex: MutableLiveData<Int> = MutableLiveData()
    val movedFromIndex: LiveData<Int>
        get() = _movedFromIndex

    private val _movedToIndex: MutableLiveData<Int> = MutableLiveData()
    val movedToIndex: LiveData<Int>
        get() = _movedToIndex

    private val _updated: MutableLiveData<Boolean> = MutableLiveData()
    val updated: LiveData<Boolean>
        get() = _updated

    fun updateRoute(itineraryId: String, currentPOIs: List<RoutedPOI>) =
        viewModelScope.launch {
            val route = repository.getByItinerary(itineraryId)
                ?: repository.create(Route(itineraryId = itineraryId))!!

            val updated = route.route.filter { it in currentPOIs }.toMutableList()
            updated.addAll(currentPOIs.filter { it !in updated })

            if (route.route != updated) {
                val distances = repository.getDistance(updated)
                updated.forEachIndexed { i, poi ->
                    poi.distance = distances[i][i + 1 % updated.size]
                }
                route.route = updated
            }

            _currentRoute.value = route
        }

    fun removePOI(routedPOI: RoutedPOI) {
        _currentRoute.value!!.route.apply {
            val index = indexOf(routedPOI)
            if (index > 0) {
                removeAt(index)
                viewModelScope.launch {
                    updateDistance(index - 1, this@apply)
                    _currentRoute.value = _currentRoute.value
                    _updated.value = true
                }
                _removedIndex.value = index
            }
        }
    }

    fun moveRoutedPOI(from: Int, to: Int) {
        _currentRoute.value!!.route.apply {
            val poi = removeAt(from)
            val toAdjusted = if (to < from)
                to
            else
                to - 1
            add(toAdjusted, poi)

            println("from: $from - to: $to - adjusted: $toAdjusted")
            viewModelScope.launch {
                updateDistance(from - 1, this@apply)
                updateDistance(toAdjusted - 1, this@apply)
                updateDistance(toAdjusted, this@apply)

                _updated.value = true
            }
            _movedFromIndex.value = from
            _movedToIndex.value = to
        }
    }

    fun autogenerate() = viewModelScope.launch {
        repository.autoGenerate(_currentRoute.value!!)?.also {
            _currentRoute.value = it
        }
    }

    suspend fun saveChanges() {
        val route = _currentRoute.value!!
        repository.update(route)
    }

    private suspend fun updateDistance(index: Int, list: List<RoutedPOI>) {
        val (from, to) =
            if (index == -1 || index == list.lastIndex) Pair(list[list.lastIndex], list[0])
            else Pair(list[index], list[index + 1])

        from.distance = try {
            repository.getDistance(listOf(from, to))[0][1]
        } catch (e: SocketTimeoutException) {
            calculateDistance(from, to)
        }
    }

    private fun calculateDistance(from: RoutedPOI, to: RoutedPOI): Double {
        val earthRadius = 6371000.0 //meters

        val dLat = Math.toRadians(to.coordinates.lat - from.coordinates.lat)
        val dLon = Math.toRadians(to.coordinates.lon - from.coordinates.lon)
        val originLat = Math.toRadians(from.coordinates.lat)
        val destinationLat = Math.toRadians(to.coordinates.lat)
        val a =
            sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(originLat) * cos(destinationLat)
        val c = 2 * asin(sqrt(a))

        return earthRadius * c
    }

    class Factory(private val repository: RouteRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RoutedPOIListViewModel(repository) as T
    }
}
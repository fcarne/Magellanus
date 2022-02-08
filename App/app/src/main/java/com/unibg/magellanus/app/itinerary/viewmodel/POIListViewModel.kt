package com.unibg.magellanus.app.itinerary.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.ItineraryRepository
import com.unibg.magellanus.app.itinerary.model.POI
import com.unibg.magellanus.app.route.model.Coordinates
import kotlinx.coroutines.launch

class POIListViewModel(private val repository: ItineraryRepository) :
    ViewModel() {

    private val _currentItinerary: MutableLiveData<Itinerary> = MutableLiveData()
    val currentItinerary: LiveData<Itinerary>
        get() = _currentItinerary

    val poiList: LiveData<List<POI>> = _currentItinerary.map { it.poiSet.toList() }

    fun load(itineraryId: String) = viewModelScope.launch {
        val itinerary = repository.getAllInfo(itineraryId)!!
        _currentItinerary.value = itinerary
    }

    fun selectPOI(poi: POI, isChecked: Boolean) {
        _currentItinerary.value?.poiSet?.find { it == poi }?.inRoute = isChecked
    }

    fun getSelectedPOI(): Pair<List<Coordinates>, List<String>> {
        _currentItinerary.value!!.poiSet.filter { it.inRoute }.apply {
            val coordinates = this.map { Coordinates(it.lat, it.lon) }
            val names = this.map { it.name!! }

            return Pair(coordinates, names)
        }
    }

    suspend fun saveChanges() {
        val itinerary = _currentItinerary.value!!
        repository.update(itinerary)
    }

    class Factory(private val repository: ItineraryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            POIListViewModel(repository) as T
    }
}
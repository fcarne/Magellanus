package com.unibg.magellanus.app.itinerary.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.ItineraryRepository
import com.unibg.magellanus.app.itinerary.model.POI
import kotlinx.coroutines.launch

class MapViewModel(itinerary: Itinerary, private val repository: ItineraryRepository) :
    ViewModel() {

    private val _currentItinerary: MutableLiveData<Itinerary> = MutableLiveData()
    val currentItinerary: LiveData<Itinerary>
        get() = _currentItinerary

    val poiSet: LiveData<Set<POI>> = _currentItinerary.map { it.poiSet.toSet() }

    private val _currentSearch: MutableLiveData<Iterable<POI>> = MutableLiveData()
    val currentSearch: LiveData<Iterable<POI>>
        get() = _currentSearch

    init {
        viewModelScope.launch {
            var i = repository.get(itinerary.id!!)
            if(i == null) i = repository.create(itinerary)

            _currentItinerary.value = i!!
        }

    }

    fun search(query: String) = viewModelScope.launch {
        _currentSearch.value = repository.search(query)
    }

    fun addPOI(poi: POI) {
        _currentItinerary.value!!.poiSet.add(poi)
    }

    fun removePOI(poi: POI) {
        _currentItinerary.value!!.poiSet.remove(poi)
    }

    suspend fun saveChanges() {
        val itinerary = _currentItinerary.value!!
        repository.update(itinerary)
    }

    class Factory(val itinerary: Itinerary, private val repository: ItineraryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MapViewModel(itinerary, repository) as T
    }
}
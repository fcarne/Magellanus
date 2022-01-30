package com.unibg.magellanus.app.itinerary.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.ItineraryRepository
import com.unibg.magellanus.app.itinerary.model.POI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapViewModel(itineraryId: String, private val repository: ItineraryRepository) :
    ViewModel() {

    private val _currentItinerary: MutableLiveData<Itinerary> = MutableLiveData()
    val currentItinerary: LiveData<Itinerary>
        get() = _currentItinerary

    val poiSet: LiveData<Set<POI>> = _currentItinerary.map { it.poiSet.toSet() }

    private val _currentSearch: MutableLiveData<MutableList<POI>> = MutableLiveData()
    val currentSearch: LiveData<MutableList<POI>>
        get() = _currentSearch

    init {
        viewModelScope.launch {
            val itinerary = repository.get(itineraryId) ?: repository.create(
                Itinerary(
                    id = itineraryId,
                    name = "Default Itinerary"
                )
            )!!

            _currentItinerary.value = itinerary
        }

    }

    fun search(query: String) = viewModelScope.launch {
        _currentSearch.value = repository.search(query).toMutableList()
    }

    fun addPOI(poi: POI) {
        _currentItinerary.value!!.poiSet.add(poi)
        val removed = _currentSearch.value?.remove(poi)
        _currentItinerary.value = _currentItinerary.value
        if(removed == true) _currentSearch.value = _currentSearch.value
    }

    fun removePOI(poi: POI) {
        _currentItinerary.value!!.poiSet.remove(poi)
        _currentItinerary.value = _currentItinerary.value
    }

    fun searchPOI(lat: Double, lon: Double): POI? = runBlocking {
        repository.getInfo(lat, lon)
    }

    suspend fun saveChanges() {
        val itinerary = _currentItinerary.value!!
        repository.update(itinerary)
    }

    class Factory(private val itineraryId: String, private val repository: ItineraryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MapViewModel(itineraryId, repository) as T
    }
}
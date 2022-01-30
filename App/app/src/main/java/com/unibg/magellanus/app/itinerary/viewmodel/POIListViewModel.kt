package com.unibg.magellanus.app.itinerary.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.ItineraryRepository
import com.unibg.magellanus.app.itinerary.model.POI
import kotlinx.coroutines.launch

class POIListViewModel(itineraryId: String, private val repository: ItineraryRepository) :
    ViewModel() {

    private val _currentItinerary: MutableLiveData<Itinerary> = MutableLiveData()
    val currentItinerary: LiveData<Itinerary>
        get() = _currentItinerary

    val poiList: LiveData<List<POI>> = _currentItinerary.map { it.poiSet.toList() }

    init {
        viewModelScope.launch {
            val itinerary = repository.getAllInfo(itineraryId)!!
            _currentItinerary.value = itinerary
        }
    }

    class Factory(private val itineraryId: String, private val repository: ItineraryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            POIListViewModel(itineraryId, repository) as T
    }
}
package com.unibg.magellanus.app.itinerary.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.ItineraryRepository
import kotlinx.coroutines.launch

class ItineraryListViewModel(private val repository: ItineraryRepository) : ViewModel() {

    private val _newItinerary: MutableLiveData<Itinerary> = MutableLiveData()
    val newItinerary: LiveData<Itinerary>
        get() = _newItinerary

    private val _removedIndex: MutableLiveData<Int> = MutableLiveData()
    val removedIndex: LiveData<Int>
        get() = _removedIndex

    private val _itineraryList: MutableLiveData<MutableList<Itinerary>> = MutableLiveData()
    val itineraryList: LiveData<MutableList<Itinerary>>
        get() = _itineraryList

    fun getItineraries(todoOrHistory: Boolean) = viewModelScope.launch {
        _itineraryList.value = repository.getAll(todoOrHistory).toMutableList()
    }

    fun createItinerary(itinerary: Itinerary) = viewModelScope.launch {
        repository.create(itinerary)?.also {
            _itineraryList.value!!.add(it)
            _newItinerary.value = it
        }
    }

    fun deleteItinerary(itinerary: Itinerary) = viewModelScope.launch {
        _itineraryList.value!!.apply {
            val index = indexOf(itinerary)
            if (index > 0) {
                _itineraryList.value!!.removeAt(index)
                repository.delete(itinerary)
                _removedIndex.value = index
            }
        }
    }

    class Factory(private val repository: ItineraryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ItineraryListViewModel(repository) as T
    }
}
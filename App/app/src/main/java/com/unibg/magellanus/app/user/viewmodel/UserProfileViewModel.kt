package com.unibg.magellanus.app.user.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.user.auth.AuthenticationProvider
import com.unibg.magellanus.app.user.model.UserAccountAPI
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserProfileViewModel(
    provider: AuthenticationProvider,
    private val api: UserAccountAPI
) : ViewModel() {

    val currentUser = UserInfoLiveData(provider)

    init {
        currentUser.observeForever {}
    }

    private val _syncedPreferences: MutableLiveData<Map<String, Any>> = MutableLiveData()
    val syncedPreferences: LiveData<Map<String, Any>?>
        get() = _syncedPreferences

    private val _successfullyDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val successfullyDeleted: LiveData<Boolean>
        get() = _successfullyDeleted

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    suspend fun savePreferences(prefs: Map<String, Any?>) {
        api.updatePreferences(currentUser.value!!.uid, prefs)
    }

    fun getPreferences() = viewModelScope.launch {
        try {
            _syncedPreferences.value = api.getPreferences(currentUser.value!!.uid)
        } catch (e: HttpException) {
            _errorMessage.value = e.message
        }
    }

    fun delete() = viewModelScope.launch {
        val response = api.delete(currentUser.value!!.uid)
        _successfullyDeleted.value = if (response.isSuccessful) {
            true
        } else {
            _errorMessage.value = response.errorBody().toString()
            false
        }
    }

    class Factory(
        private val provider: AuthenticationProvider,
        private val api: UserAccountAPI
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            UserProfileViewModel(provider, api) as T
    }

}
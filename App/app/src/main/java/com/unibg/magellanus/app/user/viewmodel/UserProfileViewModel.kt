package com.unibg.magellanus.app.user.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.user.model.UserAccountAPI
import com.unibg.magellanus.app.user.auth.UserInfo
import com.unibg.magellanus.app.user.auth.AuthenticationProvider
import kotlinx.coroutines.launch

class UserProfileViewModel(private val provider: AuthenticationProvider, private val api: UserAccountAPI) :
    ViewModel() {
    val user: UserInfo
        get() = provider.currentUser!!

    private val _syncedPreferences: MutableLiveData<Map<String, Any>> = MutableLiveData()
    val syncedPreferences: LiveData<Map<String, Any>>
        get() = _syncedPreferences

    private val _successfullyDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val successfullyDeleted: LiveData<Boolean>
        get() = _successfullyDeleted

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    @JvmSuppressWildcards
    fun savePreferences(prefs: Map<String, *>) = viewModelScope.launch {
        api.updatePreferences(user.uid, prefs)
    }

    fun getPreferences() = viewModelScope.launch {
        _syncedPreferences.value = api.getPreferences(user.uid)
    }

    fun delete() = viewModelScope.launch {
        val response = api.delete(user.uid)
        _successfullyDeleted.value = if (response.isSuccessful) {
            true
        } else {
            _errorMessage.value = response.errorBody().toString()
            false
        }
    }

    class Factory(private val provider: AuthenticationProvider, private val api: UserAccountAPI) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            UserProfileViewModel(provider, api) as T
    }
}
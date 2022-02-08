package com.unibg.magellanus.app.user.viewmodel

import androidx.lifecycle.*
import com.unibg.magellanus.app.auth.AuthenticationProvider
import com.unibg.magellanus.app.user.model.UserAccountAPI
import kotlinx.coroutines.launch

class LoginViewModel(
    private val provider: AuthenticationProvider,
    private val api: UserAccountAPI
) : ViewModel() {

    val currentUser = UserInfoLiveData(provider)

    init {
        currentUser.observeForever {}
    }

    private val _successfullySigned: MutableLiveData<Boolean> = MutableLiveData()
    val successfullySigned: LiveData<Boolean>
        get() = _successfullySigned

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun signIn() = viewModelScope.launch {
        currentUser.value!!.let { user ->
            val exists = api.checkIfExists(user.uid).isSuccessful
            if (!exists) {
                val response = api.signUp(UserAccountAPI.User(user.uid, user.email!!))
                if (response.isSuccessful)
                    _successfullySigned.value = true
                else {
                    _successfullySigned.value = false
                    provider.signOut()
                    println(response.errorBody().toString())
                    _errorMessage.value = response.errorBody().toString()
                }
            } else
                _successfullySigned.value = true
        }
    }

    class Factory(
        private val provider: AuthenticationProvider,
        private val api: UserAccountAPI
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(provider, api) as T
    }

}
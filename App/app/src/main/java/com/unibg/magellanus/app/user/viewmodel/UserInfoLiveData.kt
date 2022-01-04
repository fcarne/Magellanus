package com.unibg.magellanus.app.user.viewmodel

import androidx.lifecycle.LiveData
import com.unibg.magellanus.app.user.auth.AuthenticationProvider
import com.unibg.magellanus.app.user.auth.UserInfo

class UserInfoLiveData(private val provider: AuthenticationProvider) : LiveData<UserInfo?>() {
    private val authStateListener: AuthenticationProvider.AuthStateListener =
        AuthenticationProvider.AuthStateListener {
            this.value = provider.currentUser
        }

    init {
        authStateListener.onAuthStateChanged(provider)
    }

    override fun onActive() = provider.addAuthStateListener(authStateListener)


    override fun onInactive() = provider.removeAuthStateListener(authStateListener)

}
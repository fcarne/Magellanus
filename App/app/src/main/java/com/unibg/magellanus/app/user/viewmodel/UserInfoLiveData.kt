package com.unibg.magellanus.app.user.viewmodel

import androidx.lifecycle.LiveData
import com.unibg.magellanus.app.auth.AuthenticationProvider
import com.unibg.magellanus.app.auth.UserInfo

class UserInfoLiveData(private val provider: AuthenticationProvider) : LiveData<UserInfo?>() {
    // quando viene effettuato il login, il livedata viene valorizzato
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
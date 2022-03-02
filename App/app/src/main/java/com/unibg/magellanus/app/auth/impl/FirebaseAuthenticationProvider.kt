package com.unibg.magellanus.app.auth.impl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.unibg.magellanus.app.auth.AuthenticationProvider
import com.unibg.magellanus.app.auth.UserInfo
import java.util.*

// wrapper per la classe FirebaseAuth
class FirebaseAuthenticationProvider: AuthenticationProvider {

    private val instance: FirebaseAuth = FirebaseAuth.getInstance()
    private val listeners: MutableMap<AuthenticationProvider.AuthStateListener,FirebaseAuth.AuthStateListener>

    init {
        listeners = IdentityHashMap()
    }

    override val currentUser: UserInfo?
        get() = instance.currentUser?.let { FirebaseUserInfo(it) }

    override fun createUserWithEmailAndPassword(email: String, password: String): Task<*> =
        instance.createUserWithEmailAndPassword(email, password)

    override fun signInWithEmailAndPassword(email: String, password: String): Task<*> =
        instance.signInWithEmailAndPassword(email, password)

    override fun signInWithCredential(credential: AuthCredential): Task<*> =
        instance.signInWithCredential(credential)

    override fun resetPassword(email: String): Task<*> = instance.sendPasswordResetEmail(email)

    override fun signOut() = instance.signOut()

    override fun addAuthStateListener(listener: AuthenticationProvider.AuthStateListener) {
        val firebaseListener = FirebaseAuth.AuthStateListener {
            listener.onAuthStateChanged(this)
        }
        listeners[listener] = firebaseListener
        instance.addAuthStateListener(firebaseListener)
        println(listeners.size.toString() + "---" + listener + " ---" + firebaseListener)
    }

    override fun removeAuthStateListener(listener: AuthenticationProvider.AuthStateListener) {
        listeners[listener]?.let { instance.removeAuthStateListener(it) }
        listeners.remove(listener)
    }
}
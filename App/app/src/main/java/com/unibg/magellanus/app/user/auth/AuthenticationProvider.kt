package com.unibg.magellanus.app.user.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential

interface AuthenticationProvider {
    val currentUser: UserInfo?
    fun createUserWithEmailAndPassword(email : String, password : String) : Task<*>
    fun signInWithEmailAndPassword(email : String, password : String) : Task<*>
    fun signInWithCredential(credential: AuthCredential): Task<*>
    fun resetPassword(email: String): Task<*>
    fun signOut()
    fun isUserLoggedIn(): Boolean
}
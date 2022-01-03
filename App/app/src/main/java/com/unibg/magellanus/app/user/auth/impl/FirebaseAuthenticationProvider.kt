package com.unibg.magellanus.app.user.auth.impl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.unibg.magellanus.app.user.auth.AuthenticationProvider
import com.unibg.magellanus.app.user.auth.UserInfo

class FirebaseAuthenticationProvider(private val instance: FirebaseAuth = FirebaseAuth.getInstance()) :
    AuthenticationProvider {

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

    override fun isUserLoggedIn(): Boolean = currentUser != null
}
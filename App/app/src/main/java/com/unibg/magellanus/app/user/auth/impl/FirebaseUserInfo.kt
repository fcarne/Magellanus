package com.unibg.magellanus.app.user.auth.impl

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.unibg.magellanus.app.user.auth.UserInfo


class FirebaseUserInfo(private val user: FirebaseUser) : UserInfo {
    override val uid
        get() = user.uid
    override val displayName
        get() = user.displayName
    override val email
        get() = user.email
    override val providerId
        get() = user.providerId
    override val photoUrl: Uri?
        get() = user.photoUrl

    override fun getToken(refresh: Boolean): String? {
        var token: String? = null
        user.getIdToken(refresh).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result!!.token
            }
        }
        return token
    }

    override fun delete(): Task<Void> = user.delete()
    override fun updatePassword(password: String): Task<Void> = user.updatePassword(password)
    override fun isNewUser(): Boolean =
        user.metadata?.run { creationTimestamp == lastSignInTimestamp } ?: false
}

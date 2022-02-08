package com.unibg.magellanus.app.auth

import android.net.Uri
import com.google.android.gms.tasks.Task

interface UserInfo {
    val uid: String
    val email: String?
    val displayName: String?
    val providerId: String
    val photoUrl: Uri?
    fun getToken(refresh: Boolean): String?
    fun isNewUser(): Boolean
    fun delete(): Task<Void>
    fun updatePassword(password: String): Task<Void>

}
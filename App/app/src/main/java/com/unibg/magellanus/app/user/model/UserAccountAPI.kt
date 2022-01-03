package com.unibg.magellanus.app.user.model

import com.unibg.magellanus.app.common.network.AuthInterceptor
import com.unibg.magellanus.app.user.auth.AuthenticationProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface UserAccountAPI {
    @HEAD("{uid}")
    @Headers(AuthInterceptor.NO_AUTH_HEADER)
    suspend fun checkIfExists(@Path("uid") uid: String): Response<Void>

    @POST(".")
    @Headers(AuthInterceptor.NO_AUTH_HEADER)
    suspend fun signUp(@Body user: User): Response<Void>

    @DELETE("{uid}")
    suspend fun delete(@Path("uid") uid: String): Response<Void>

    @PATCH("{uid}/preferences")
    suspend fun updatePreferences(@Path("uid") uid: String, @Body preferences: Map<String, *>): Response<Void>

    @GET("{uid}/preferences")
    suspend fun getPreferences(@Path("uid") uid: String): Map<String, Any>?

    data class User (val uid: String, val email: String)

    companion object {
        var BASE_URL = "http://10.0.2.2:5000/users/"
        fun create(provider: AuthenticationProvider): UserAccountAPI {
            val authInterceptor = AuthInterceptor(provider)
            val httpInterceptor =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder().addInterceptor(authInterceptor)
                .addInterceptor(httpInterceptor).build()
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            return retrofit.create(UserAccountAPI::class.java)
        }
    }
}



package com.unibg.magellanus.app.route.model.network

import com.unibg.magellanus.app.common.network.AuthInterceptor
import com.unibg.magellanus.app.route.model.Route
import com.unibg.magellanus.app.user.auth.AuthenticationProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RouteAPI {
    @GET("{id}")
    suspend fun get(@Path("id") id: String): Response<Route?>

    @GET("itinerary/{id}")
    suspend fun getByItinerary(@Path("id") id: String): Response<Route?>

    @POST(".")
    suspend fun create(@Body route: Route): Response<Route?>

    @PUT("me/{id}")
    suspend fun updateMine(@Path("id") id: String, @Body route: Route): Response<Void>

    @DELETE("me/{id}")
    suspend fun deleteMine(@Path("id") id: String): Response<Void>

    @POST("auto")
    suspend fun autoGenerate(@Body route: Route): Response<Route?>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/api/routes/"
        fun create(provider: AuthenticationProvider): RouteAPI {
            val authInterceptor = AuthInterceptor(provider)
            val httpInterceptor =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(httpInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            return retrofit.create(RouteAPI::class.java)
        }
    }
}
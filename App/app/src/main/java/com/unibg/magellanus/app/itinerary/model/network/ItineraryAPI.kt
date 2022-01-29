package com.unibg.magellanus.app.itinerary.model.network

import com.unibg.magellanus.app.common.network.AuthInterceptor
import com.unibg.magellanus.app.common.network.CacheControlInterceptor
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.user.auth.AuthenticationProvider
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

interface ItineraryAPI {
    @GET("{id}")
    suspend fun get(@Path("id") id: String): Itinerary?

    @POST(".")
    suspend fun create(@Body itinerary: Itinerary): Itinerary?

    @PUT("me/{id}")
    suspend fun updateMine(@Path("id") id: String, @Body itinerary: Itinerary): Response<Void>

    @DELETE("me/{id}")
    suspend fun deleteMine(@Path("id") id: String): Response<Void>

    @GET("me")
    suspend fun findMine(@Query("completed") completed: Boolean?): List<Itinerary>

    companion object {
        var BASE_URL = "http://10.0.2.2:8080/api/itineraries/"
        fun create(provider: AuthenticationProvider, cacheDir: File): ItineraryAPI {
            val authInterceptor = AuthInterceptor(provider)
            val httpInterceptor =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val cacheInterceptor = CacheControlInterceptor()

            //setup cache
            val httpCacheDirectory = File(cacheDir, "itinerary-cache")
            val cacheSize = (10 * 1024 * 1024).toLong() // 10 MiB

            val cache = Cache(httpCacheDirectory, cacheSize)

            val client = OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(authInterceptor)
                .addInterceptor(httpInterceptor)
                .cache(cache)
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            return retrofit.create(ItineraryAPI::class.java)
        }
    }
}
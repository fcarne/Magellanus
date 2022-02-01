package com.unibg.magellanus.app.route.model.network

import com.unibg.magellanus.app.common.network.CacheControlInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File
import java.util.concurrent.TimeUnit

data class MatrixResponse(val distances: List<List<Double>>)

interface MatrixAPI {

    @GET("table/v1/foot/{coordinates}?annotations=distance")
    suspend fun getDistances(@Path("coordinates") coordinates: String): MatrixResponse

    companion object {
        private const val BASE_URL = "http://router.project-osrm.org/"
        fun create(cacheDir: File): MatrixAPI {
            val httpInterceptor =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val cacheInterceptor = CacheControlInterceptor()

            //setup cache
            val httpCacheDirectory = File(cacheDir, "matrix-cache")
            val cacheSize = (10 * 1024 * 1024).toLong() // 10 MiB

            val cache = Cache(httpCacheDirectory, cacheSize)

            val client = OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(httpInterceptor)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .cache(cache)
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            return retrofit.create(MatrixAPI::class.java)
        }
    }
}
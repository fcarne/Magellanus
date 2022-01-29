package com.unibg.magellanus.app.itinerary.model.network

import com.google.gson.*
import com.unibg.magellanus.app.common.network.CacheControlInterceptor
import com.unibg.magellanus.app.itinerary.model.Address
import com.unibg.magellanus.app.itinerary.model.POI
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.lang.reflect.Type

interface GeocodingAPI {

    @GET("api")
    suspend fun search(@Query("q") query: String, @Query("limit") limit: Int?): Iterable<POI>

    @GET("reverse")
    suspend fun reverseSearch(@Query("lat") lat: Double, @Query("lon") lon: Double): POI

    companion object {
        var BASE_URL = "https://photon.komoot.io/"
        fun create(cacheDir: File): GeocodingAPI {
            val httpInterceptor =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val cacheInterceptor = CacheControlInterceptor()

            //setup cache
            val httpCacheDirectory = File(cacheDir, "geocode-cache")
            val cacheSize = (10 * 1024 * 1024).toLong() // 10 MiB

            val cache = Cache(httpCacheDirectory, cacheSize)

            val client = OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(httpInterceptor)
                .cache(cache)
                .build()

            val gson =
                GsonBuilder().registerTypeAdapter(POI::class.java, OsmPOIDeserializer()).create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            return retrofit.create(GeocodingAPI::class.java)
        }
    }
}

class OsmPOIDeserializer : JsonDeserializer<POI> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): POI {
        val response = json!!.asJsonObject
        val properties = response.getAsJsonObject("properties")
        val address = Gson().fromJson(properties, Address::class.java)

        val name = properties["name"].asString
        val coordinates = response.getAsJsonObject("geometry").getAsJsonArray("coordinates")

        return POI(name, coordinates[0].asDouble, coordinates[1].asDouble, address)
    }
}
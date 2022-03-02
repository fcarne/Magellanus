package com.unibg.magellanus.app.common.network

import com.unibg.magellanus.app.auth.AuthenticationProvider
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val provider: AuthenticationProvider) : Interceptor {
    companion object {
        private const val CUSTOM_HEADER = "@"
        const val NO_AUTH_HEADER = "${CUSTOM_HEADER}: NoAuth"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val builder = request.newBuilder()

        // aggiunge se necessario l'header di autenticazione alla richiesta
        request = if (request.headers[CUSTOM_HEADER] != null) {
            builder.removeHeader(CUSTOM_HEADER)
        } else {
            builder.addHeader("Authorization", "Bearer ${provider.currentUser?.getToken(false)}")
        }.build()

        return chain.proceed(request)
    }

}

package xyz.bluepencil.bluepencil

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Url
import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


private const val BASE_URL = "https://127.0.0.1:9200/v1/vault-ethereum/"


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getProperties] method
 */
interface VaultApiService {
    /**
     * Returns a Retrofit callback that delivers a String
     * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
     * HTTP method
     */
    @PUT
    @Headers("X-Vault-Request: true", "X-Vault-Token: hvs.czhNY0NBcRvM15O5Nme2m8Mz")

    fun registerUser(@Url url: String, @Body data: Data): Call<Any>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object VaultApi {
    val retrofitService : VaultApiService by lazy { retrofit.create(VaultApiService::class.java) }
}


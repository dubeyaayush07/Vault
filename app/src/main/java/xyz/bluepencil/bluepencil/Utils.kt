package xyz.bluepencil.bluepencil

import android.content.Context
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

fun getCurrencyString(num: Int): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.setMaximumFractionDigits(0)
    format.setCurrency(Currency.getInstance("INR"))

    return format.format(num)
}

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    return formatter.format(date)
}

fun generateSecureOkHttpClient(context: Context): OkHttpClient {
    // Create a simple builder for our http client, this is only por example purposes
    var httpClientBuilder = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)

    // Here you may wanna add some headers or custom setting for your builder

    // Get the file of our certificate
    var caFileInputStream = context.resources.openRawResource(R.raw.my_certificate)

    // We're going to put our certificates in a Keystore
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(caFileInputStream, "my file password".toCharArray())

    // Create a KeyManagerFactory with our specific algorithm our our public keys
    // Most of the cases is gonna be "X509"
    val keyManagerFactory = KeyManagerFactory.getInstance("X509")
    keyManagerFactory.init(keyStore, "my file password".toCharArray())

    // Create a SSL context with the key managers of the KeyManagerFactory
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.keyManagers, null, SecureRandom())

    val trustManagerFactory: TrustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val trustManagers: Array<TrustManager> = trustManagerFactory.getTrustManagers()
    check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
        "Unexpected default trust managers:" + Arrays.toString(
            trustManagers
        )
    }
    val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager
    //Finally set the sslSocketFactory to our builder and build it
    return httpClientBuilder
        .sslSocketFactory(sslContext.socketFactory, trustManager)
        .build()
}
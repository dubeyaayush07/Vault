package xyz.bluepencil.bluepencil.ui


import android.os.Bundle
import android.util.Base64

import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.google.firebase.installations.Utils
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import xyz.bluepencil.bluepencil.*
import xyz.bluepencil.bluepencil.databinding.FragmentHomeBinding
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec


class HomeFragment : Fragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                   requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitBtn.setOnClickListener {
          register(binding.name.text.toString(), binding.mnemon.text.toString())
        }

    }

    private fun register(name: String, mne: String) {

        var d = Data()
        d.mnemonic = mne

        val pubCertInputStream = context?.resources?.openRawResource(R.raw.my_certificate)
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val x509Certificate: X509Certificate = cf.generateCertificate(pubCertInputStream) as X509Certificate //1

        val keyPair = KeyPair(x509Certificate.publicKey, loadPrivateKey()) //2

        val handshakeCertificates: HandshakeCertificates = HandshakeCertificates.Builder()
            .addPlatformTrustedCertificates()
            .heldCertificate(HeldCertificate(keyPair, x509Certificate))
            .build() //3

        val client = OkHttpClient
            .Builder()
            .sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager)
            .build() //4


        val BASE_URL = "https://9200-rishabhmali-mfaenablede-womg985qwtt.ws-eu47.gitpod.io/v1/vault-ethereum/"
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

            retrofit.create(VaultApiService::class.java).registerUser("accounts/$name", d).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {

                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadPrivateKey(): PrivateKey? {
        val encoded: ByteArray = Base64.decode(getString(R.string.cert), Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(encoded)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }









}
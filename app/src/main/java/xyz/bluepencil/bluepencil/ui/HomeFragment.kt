package xyz.bluepencil.bluepencil.ui


import android.os.Bundle

import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.google.firebase.installations.Utils
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import xyz.bluepencil.bluepencil.*
import xyz.bluepencil.bluepencil.databinding.FragmentHomeBinding




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



        val BASE_URL = "https://9200-rishabhmali-mfaenablede-womg985qwtt.ws-eu47.gitpod.io/v1/vault-ethereum/"
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

            retrofit.create(VaultApiService::class.java).registerUser("accounts/$name", d).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {

                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            }
        })
    }









}
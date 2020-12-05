package com.thuraaung.notes.uitls

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NetworkUtils {

    private val networkLiveData: MutableLiveData<Boolean> = MutableLiveData(false)


    fun getNetworkLiveData(context: Context): LiveData<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                networkLiveData.postValue(true)
            }

            override fun onLost(network: Network) {
                networkLiveData.postValue(false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        return networkLiveData
    }
}

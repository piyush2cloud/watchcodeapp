package com.immersion.neuro.connection.network.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.immersion.neuro.model.HeartInfo
import com.immersion.neuro.model.Measurements
import com.immersion.neuro.connection.network.NetworkAPI
import com.immersion.neuro.connection.network.NetworkConstants
import com.immersion.neuro.connection.network.NetworkService
import retrofit2.Call
import retrofit2.Response

object HeartRateRepository {
    private var TAG: String = "HeartRateRepository"
    private var networkAPI: NetworkAPI = NetworkService.createService(NetworkAPI::class.java)

    fun sendHeartData(heartRateValue: Int): MutableLiveData<JsonElement> {
        val apiResponse = MutableLiveData<JsonElement>()
        val authHeaders: HashMap<String, String> = HashMap()
        authHeaders["Authorization"] = NetworkConstants.TOKEN

        networkAPI.sendHeartRate(NetworkConstants.HEART_RATE_URL, HeartInfo().apply {
            identifier = "ble"
            deviceType = "android"
            measurements = arrayOf(Measurements().apply {
                heartRate = heartRateValue
                timeStamp = System.currentTimeMillis()
            })
        }, authHeaders)
            .enqueue(object : retrofit2.Callback<JsonElement?> {
                override fun onResponse(
                    call: Call<JsonElement?>,
                    response: Response<JsonElement?>
                ) {
                    if (response.isSuccessful){
                        apiResponse.value = response.body()
                        Log.d(TAG, HeartInfo().apply {
                            identifier = "ble"
                            deviceType = "android"
                            measurements = arrayOf(Measurements().apply {
                                heartRate = heartRateValue
                                timeStamp = System.currentTimeMillis()
                            })
                        }.toString())
                    }
                }
                override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                    apiResponse.value = null
                }
            })

        return apiResponse;
    }
}


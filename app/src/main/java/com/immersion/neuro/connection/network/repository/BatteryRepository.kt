package com.immersion.neuro.connection.network.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.immersion.neuro.model.BatteryInfo
import com.immersion.neuro.connection.network.NetworkAPI
import com.immersion.neuro.connection.network.NetworkConstants
import com.immersion.neuro.connection.network.NetworkService
import retrofit2.Call
import retrofit2.Response

object BatteryRepository {
    private var networkAPI: NetworkAPI = NetworkService.createService(NetworkAPI::class.java)

    fun sendBatteryData(batteryValue: Int): MutableLiveData<JsonElement> {
        val apiResponse = MutableLiveData<JsonElement>()
        val authHeaders: HashMap<String, String> = HashMap()
        authHeaders["Authorization"] = NetworkConstants.TOKEN

        networkAPI.sendBatteryInfo(NetworkConstants.BATTERY_INFO_URL, BatteryInfo().apply {
            identifier = "alexa12345"
            entryTime = System.currentTimeMillis()
        }, authHeaders)
            .enqueue(object : retrofit2.Callback<JsonElement?> {
                override fun onResponse(
                    call: Call<JsonElement?>,
                    response: Response<JsonElement?>
                ) {
                    if (response.isSuccessful)
                        apiResponse.value = response.body()
                }

                override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                    apiResponse.value = null
                }
            })

        return apiResponse;
    }
}


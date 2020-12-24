package com.immersion.neuro.connection.network

import com.google.gson.JsonElement
import com.immersion.neuro.model.BatteryInfo
import com.immersion.neuro.model.HeartInfo
import retrofit2.Call
import retrofit2.http.*

interface NetworkAPI {
    @PUT
    fun sendHeartRate(
        @Url url: String,
        @Body body: HeartInfo,
        @HeaderMap headers: Map<String, String>
    ): Call<JsonElement>

    @PUT
    fun sendBatteryInfo(
        @Url url: String,
        @Body body: BatteryInfo,
        @HeaderMap headers: Map<String, String>
    ): Call<JsonElement>
}
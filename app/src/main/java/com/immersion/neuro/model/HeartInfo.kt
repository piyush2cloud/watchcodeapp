package com.immersion.neuro.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HeartInfo(
    @SerializedName("identifier")
    @Expose
    var identifier: String?=null,
    @SerializedName("device_type")
    @Expose
    var deviceType: String?=null,
    @SerializedName("measurements")
    @Expose
    var measurements: Array<Measurements>?=null
)

data class Measurements(
    @SerializedName("time_stamp")
    @Expose
    var timeStamp: Long=0,
    @SerializedName("heart_rate")
    @Expose
    var heartRate: Int=0
)
package com.immersion.neuro.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BatteryInfo(
    @SerializedName("identifier")
    @Expose
    var identifier: String? = null,
    @SerializedName("entry_time")
    @Expose
    var entryTime: Long = 0,
    @SerializedName("action")
    @Expose
    var action: String? = null,
    @SerializedName("nt")
    @Expose
    var nt: String? = null,
    @SerializedName("did")
    @Expose
    var did: String? = null,
    @SerializedName("aid")
    @Expose
    var aid: String? = null,
    @SerializedName("btry")
    @Expose
    var btry: String? = null,
    @SerializedName("mid")
    @Expose
    var mid: String? = null,
    @SerializedName("mn")
    @Expose
    var mn: String? = null,
    @SerializedName("ms")
    @Expose
    var ms: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("cv")
    @Expose
    var cv: String? = null,
    @SerializedName("cn")
    @Expose
    var cn: String? = null,
    @SerializedName("osn")
    @Expose
    var osn: String? = null,
    @SerializedName("osv")
    @Expose
    var osv: String? = null,
    @SerializedName("lat")
    @Expose
    var lat: Long = 0,
    @SerializedName("lon")
    @Expose
    var lon: Long = 0,
    @SerializedName("cau")
    @Expose
    var cau: String? = null,
    @SerializedName("ha")
    @Expose
    var ha: Boolean = true
)


package com.immersion.neuro.connection.ble


object GattPropertiesMapping {

    enum class GattProperty(val value: String) {
        HEART_RATE_MEASUREMENT("00002a37-0000-1000-8000-00805f9b34fb"),
        HEART_RATE_CONTROL_POINT("00002a38-0000-1000-8000-00805f9b34fb"),
        BATTERY_LEVEL("00002a19-0000-1000-8000-00805f9b34fb"),
        BATTERY_POWER_STATE("00002a1a-0000-1000-8000-00805f9b34fb"),
        BATTERY_LEVEL_STATE("00002a1b-0000-1000-8000-00805f9b34fb"),
        DEVICE_NAME("00002a00-0000-1000-8000-00805f9b34fb"),
        MANUFACTURE_NAME("00002a29-0000-1000-8000-00805f9b34fb"),
        SERIAL_NAME("00002a25-0000-1000-8000-00805f9b34fb"),
        MODEL_SERIAL_NAME("00002a24-0000-1000-8000-00805f9b34fb"),
        BATTERY_SERVICE("0000180f-0000-1000-8000-00805f9b34fb"),
        HEART_SERVICE("0000180d-0000-1000-8000-00805f9b34fb"),
        DEVICE_INFO_SERVICE("0000180a-0000-1000-8000-00805f9b34fb"),
        CURRENT_TIME_SERVICE("00001805-0000-1000-8000-00805f9b34fb"),
        HEART_MEASRUE_DESCRIPTOR("00002902-0000-1000-8000-00805f9b34fb")
    }


}
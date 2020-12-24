package com.example.wearosactivity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.ExecutionException
import android.support.wearable.activity.WearableActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class WearActivity : WearableActivity(), SensorEventListener {
    var datapath = "/message_path"
    var mHeartRateSensor: Sensor? = null
    private var mSensorManager: SensorManager? = null
    var batteryLevel: String? = null
    var heartRateLevel: String? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear)

        // Register the local broadcast receiver to receive messages from the listener.
        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = MessageReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        // Enables Always-on
        setAmbientEnabled()
    }

    private fun registerHeartBatteryListener() {
        mHeartRateSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        mSensorManager!!.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        this.registerReceiver(mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val message = intent.getStringExtra("message")
            if (message.equals("START", ignoreCase = true)) {
                val mWearBodySensorsPermissionApproved =
                    (ActivityCompat.checkSelfPermission(
                        this@WearActivity,
                        Manifest.permission.BODY_SENSORS
                    )
                            == PackageManager.PERMISSION_GRANTED)
                if (!mWearBodySensorsPermissionApproved) {
                    ActivityCompat.requestPermissions(
                        this@WearActivity,
                        arrayOf(Manifest.permission.BODY_SENSORS),
                        PERMISSION_REQUEST_READ_BODY_SENSORS
                    )
                } else {
                    registerHeartBatteryListener()
                }
            }
            if (message.equals("STOP", ignoreCase = true)) {
                if (mSensorManager != null) {
                    unregisterAllReceivers()
                }
            }
        }
    }

    fun unregisterAllReceivers() {
        mSensorManager?.unregisterListener(this@WearActivity)
        unregisterReceiver(mBatInfoReceiver)
    }

    //This actually sends the message to the wearable device.
    internal inner class SendThread //constructor
        (var path: String, var message: String) :
        Thread() {

        //sends the message via the thread.  this will send to all wearables connected, but
        //since there is (should only?) be one, so no problem.
        override fun run() {
            //first get all the nodes, ie connected wearable devices.
            val nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).connectedNodes
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                val nodes =
                    Tasks.await(
                        nodeListTask
                    )

                //Now send the message to each device.
                for (node in nodes) {
                    val sendMessageTask =
                        Wearable.getMessageClient(this@WearActivity)
                            .sendMessage(node.id, path, message.toByteArray())
                    try {
                        // Block on a task and get the result synchronously (because this is on a background
                        // thread).
                        val result = Tasks.await(sendMessageTask)
                        Log.v(
                            TAG,
                            "SendThread: message send to " + node.displayName
                        )
                    } catch (exception: ExecutionException) {
                        Log.e(TAG, "Task failed: $exception")
                    } catch (exception: InterruptedException) {
                        Log.e(
                            TAG,
                            "Interrupt occurred: $exception"
                        )
                    }
                }
            } catch (exception: ExecutionException) {
                Log.e(TAG, "Task failed: $exception")
            } catch (exception: InterruptedException) {
                Log.e(TAG, "Interrupt occurred: $exception")
            }
        }

    }

    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            batteryLevel = Integer.toString(level)
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_HEART_RATE) {
            val msg = "" + sensorEvent.values[0].toInt()
            heartRateLevel = msg
            val combined =
                heartRateLevel + "::" + batteryLevel + "::" + Build.MANUFACTURER
            SendThread(datapath, combined).start()
        } else Log.d(TAG, "Unknown sensor type")
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_READ_BODY_SENSORS) {
            if (grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                val message =
                    heartRateLevel + "::" + batteryLevel + "::" + Build.MANUFACTURER
                SendThread(datapath, message).start()
                registerHeartBatteryListener()
            } else {
                Toast.makeText(
                    this@WearActivity,
                    "PLEASE ALLOW SENSOR PERMISSIO ON WEAR OS",
                    Toast.LENGTH_SHORT
                )
            }
        }
    }

    companion object {
        private const val TAG = "Wear MainActivity"
        private const val PERMISSION_REQUEST_READ_BODY_SENSORS = 1
    }
}

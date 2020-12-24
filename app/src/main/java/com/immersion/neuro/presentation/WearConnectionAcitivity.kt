package com.immersion.neuro.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.immersion.neuro.R
import java.util.concurrent.ExecutionException


open class WearConnectionAcitivity : AppCompatActivity(), View.OnClickListener {
    var datapath = "/message_path"
    var mStartButton: Button? = null
    var mStopButton: Button? = null
    var mHeart: TextView? = null
    var mBattery: TextView? = null
    var mDevice: TextView? = null
    protected var handler: Handler? = null
    var TAG = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pair_ble2)
        //get the widgets

    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_wear_connection_acitivity)
//        //get the widgets
//        mStartButton = findViewById(R.id.sendbtn)
//        mStopButton = findViewById(R.id.stopBtn)
//
//        mHeart = findViewById(R.id.heartRate)
//        mBattery = findViewById(R.id.battery)
//        mDevice = findViewById(R.id.device)
//
//        mStartButton?.setOnClickListener(View.OnClickListener {
//            SendThread(datapath, "START").start();
//        })
//
//        // Register the local broadcast receiver
//        val messageFilter = IntentFilter(Intent.ACTION_SEND)
//        val messageReceiver: MessageReceiver = MessageReceiver()
//        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter)
//    }

    //setup a broadcast receiver to receive the messages from the wear device via the listenerService.
    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val message = intent.getStringExtra("message")
            Log.v(TAG, "Main activity received message: $message")
            val arrayList = message!!.split("::").toTypedArray()
            if (arrayList.size > 2) {
                mHeart!!.text = arrayList[0]
                mBattery!!.text = arrayList[1]
                mDevice!!.text = arrayList[2]
            }
        }
    }

    //button listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.sendbtn -> SendThread(datapath, "START").start()
            R.id.stopBtn -> SendThread(datapath, "STOP").start()
            else -> {
            }
        }
    }

    //This actually sends the message to the wearable device.
    internal inner class SendThread //constructor
        (var path: String, var message: String) :
        Thread() {
        override fun run() {

            //first get all the nodes, ie connected wearable devices.
            val nodeListTask: Task<List<Node>> =
                Wearable.getNodeClient(applicationContext).getConnectedNodes()
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                val nodes: List<Node> = Tasks.await(nodeListTask)

                //Now send the message to each device.
                for (node in nodes) {
                    val sendMessageTask: Task<Int> =
                        Wearable.getMessageClient(this@WearConnectionAcitivity)
                            .sendMessage(node.getId(), path, message.toByteArray())
                    try {
                        val result: Int = Tasks.await(sendMessageTask)
                        Log.v(
                            TAG,
                            "SendThread: message send to " + node.getDisplayName()
                        )
                    } catch (exception: ExecutionException) {
                        Log.e(TAG, "Send Task failed: $exception")
                    } catch (exception: InterruptedException) {
                        Log.e(TAG, "Send Interrupt occurred: $exception")
                    }
                }
            } catch (exception: ExecutionException) {

                Log.e(TAG, "Node Task failed: $exception")
            } catch (exception: InterruptedException) {
                Log.e(TAG, "Node Interrupt occurred: $exception")
            }
        }

    }
}

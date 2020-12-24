package com.immersion.neuro.presentation

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class ListenerService : WearableListenerService() {
    var TAG = "WearableListenerService"

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.getPath().equals("/message_path")) {
            val message = String(messageEvent.getData())
            Log.v(TAG, "Message path received on phone is: " + messageEvent.getPath())
            Log.v(TAG, "Message received on phone is: $message")

            // Broadcast message to MainActivity for display
            val messageIntent = Intent()
            messageIntent.action = Intent.ACTION_SEND
            messageIntent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)
        } else {
            super.onMessageReceived(messageEvent)
        }
    }
}

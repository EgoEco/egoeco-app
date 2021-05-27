package com.example.egoeco_app.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothBroadcastReceiver : BroadcastReceiver() {
    lateinit var listener: BluetoothBroadcastReceiverListener

    interface BluetoothBroadcastReceiverListener {
        fun onScanStateChanged(state: Int)
        fun onPairStateChanged(state: Int)
        fun onConnectStateChanged(state: Int)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("KHJ", "in BluetoothBroadcastReceiver")
        intent?.let {
            val scanState = intent.getIntExtra("scan", -2)
            val pairState = intent.getIntExtra("pair", -2)
            val connectState = intent.getIntExtra("connect", -2)
            if (scanState != -2) {
                listener.onScanStateChanged(scanState)
                Log.d("KHJ","scanState: $scanState")
            }
            if (pairState != -2) {
                listener.onPairStateChanged(pairState)
                Log.d("KHJ","pairState: $pairState")
            }
            if (connectState != -2) {
                listener.onConnectStateChanged(connectState)
                Log.d("KHJ","connectState: $connectState")
            }
        }
    }

    fun setBluetoothBroadCastReceiverListener(listener: BluetoothBroadcastReceiverListener) {
        this.listener = listener
    }
}
package com.example.egoeco_app.model.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.egoeco_app.utils.DevTool.logD

class BluetoothBroadcastReceiver : BroadcastReceiver() {
    lateinit var listener: BluetoothBroadcastReceiverListener

    companion object {
    }

    interface BluetoothBroadcastReceiverListener {
        fun onScanStateChanged(state: Int)
        fun onPairStateChanged(state: Int)
        fun onConnectStateChanged(state: Int)
        fun onBluetoothStateChanged(pair: Pair<BluetoothState, Int>)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        logD( "in BluetoothBroadcastReceiver")
        intent?.let {
            val scanState = intent.getIntExtra("scan", -2)
            val pairState = intent.getIntExtra("pair", -2)
            val connectState = intent.getIntExtra("connect", -2)
            if (scanState != -2) {
                listener.onScanStateChanged(scanState)
                listener.onBluetoothStateChanged(Pair(BluetoothState.SCAN, scanState))
                logD( "scanState: $scanState")
            }
            if (pairState != -2) {
                listener.onPairStateChanged(pairState)
                listener.onBluetoothStateChanged(Pair(BluetoothState.PAIR, pairState))
                logD( "pairState: $pairState")
            }
            if (connectState != -2) {
                listener.onConnectStateChanged(connectState)
                listener.onBluetoothStateChanged(Pair(BluetoothState.CONNECT, connectState))
                logD( "connectState: $connectState")
            }
        }
    }

    fun setBluetoothBroadCastReceiverListener(listener: BluetoothBroadcastReceiverListener) {
        this.listener = listener
    }
}
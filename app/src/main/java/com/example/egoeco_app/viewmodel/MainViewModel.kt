package com.example.egoeco_app.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.egoeco_app.model.*
import com.example.egoeco_app.view.MainActivity
import com.github.zakaprov.rxbluetoothadapter.RxBluetoothAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject internal constructor(
    application: Application,
    private val dataRepository: DataRepository
) : AndroidViewModel(application) {
    val obdDataList = MutableLiveData<List<OBDData>>()
    val bluetoothState = MutableLiveData<Pair<BluetoothState, Int>>(Pair(BluetoothState.SCAN, -1))
    val scanState = MutableLiveData<Int>(-1)
    val pairState = MutableLiveData<Int>(-1)
    val connectState = MutableLiveData<Int>(-1)

    private lateinit var bluetoothBroadcastReceiver: BluetoothBroadcastReceiver


    companion object {
        const val LOCATION_PERMISSION_CODE = 1
    }

    init {
        getAllOBDData()
    }

    fun startService() {
//        Log.d("KHJ", "ViewModel startService")
        val serviceIntent = Intent(getApplication(), BluetoothService::class.java)
        serviceIntent.action = BluetoothService.ACTION_START
        getApplication<Application>().startForegroundService(serviceIntent)
        bluetoothBroadcastReceiver = BluetoothBroadcastReceiver()
        bluetoothBroadcastReceiver.setBluetoothBroadCastReceiverListener(object :
            BluetoothBroadcastReceiver.BluetoothBroadcastReceiverListener {
            override fun onScanStateChanged(state: Int) {
                scanState.value = state
            }

            override fun onPairStateChanged(state: Int) {
                pairState.value = state
            }

            override fun onConnectStateChanged(state: Int) {
                connectState.value = state
            }

            override fun onBluetoothStateChanged(pair: Pair<BluetoothState, Int>) {
                bluetoothState.value = pair
                Log.d("KHJ","bluetoothState.value: $pair")
                if (pair.second == -1) { // failed in scanning or pairing or connecting
                    Log.d("KHJ","pair.second == -1. stopService()")
                    stopService()
                }
            }
        }
        )
        val intentFilter = IntentFilter("bluetoothServiceIntent")
        LocalBroadcastManager.getInstance(getApplication())
            .registerReceiver(bluetoothBroadcastReceiver, intentFilter)
    }

    fun stopService() {
        val serviceIntent = Intent(getApplication(), BluetoothService::class.java)
        serviceIntent.action = BluetoothService.ACTION_STOP
        getApplication<Application>().stopService(serviceIntent)
        LocalBroadcastManager.getInstance(getApplication())
            .unregisterReceiver(bluetoothBroadcastReceiver)
        scanState.value = -1
        pairState.value = -1
        connectState.value = -1
        bluetoothState.value = Pair(BluetoothState.CONNECT,-1) // Service가 너무 빨리 죽어서 여기서 value
    }

    fun insertOBDData(data: OBDData) {
        dataRepository.getOBDDataRepository().insertOBDData(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CompletableObserver {
                override fun onSubscribe(d: Disposable?) {
                    Log.d("KHJ", "insertOBDData() onSubscribe $d")
                }

                override fun onComplete() {
                    Log.d("KHJ", "insertOBDData() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "insertOBDData() onError $e")
                }
            })
    }

    fun updateOBDData(data: OBDData) {
        dataRepository.getOBDDataRepository().updateOBDData(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CompletableObserver {
                override fun onSubscribe(d: Disposable?) {
                    Log.d("KHJ", "updateOBDData() onSubscribe $d")
                }

                override fun onComplete() {
                    Log.d("KHJ", "updateOBDData() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "updateOBDData() onError $e")
                }
            })
    }

    fun deleteOBDData(data: OBDData) {
        dataRepository.getOBDDataRepository().deleteOBDData(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CompletableObserver {
                override fun onSubscribe(d: Disposable?) {
                    Log.d("KHJ", "deleteOBDData() onSubscribe $d")
                }

                override fun onComplete() {
                    Log.d("KHJ", "deleteOBDData() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "deleteOBDData() onError $e")
                }
            })
    }

    fun deleteAllOBDData() {
        dataRepository.getOBDDataRepository().deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CompletableObserver {
                override fun onSubscribe(d: Disposable?) {
                    Log.d("KHJ", "deleteOBDData() onSubscribe $d")
                }

                override fun onComplete() {
                    Log.d("KHJ", "deleteOBDData() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "deleteOBDData() onError $e")
                }
            })
    }

    fun deleteOBDDataById(id: Long) {
        dataRepository.getOBDDataRepository().deleteOBDDataById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CompletableObserver {
                override fun onSubscribe(d: Disposable?) {
                    Log.d("KHJ", "deleteOBDDataById() onSubscribe $d")
                }

                override fun onComplete() {
                    Log.d("KHJ", "deleteOBDDataById() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "deleteOBDDataById() onError $e")
                }
            })
    }

    private fun getAllOBDData() {
        dataRepository.getOBDDataRepository().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : Observer<List<OBDData>> {
                override fun onSubscribe(d: Disposable?) {
                    Log.d("KHJ", "getAllOBDData() onSubscribe $d")
                }

                override fun onNext(t: List<OBDData>?) {
                    t?.let {
                        obdDataList.value = it.toList()
                    }
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "getAllOBDData() onError $e")
                }

                override fun onComplete() {
                    Log.d("KHJ", "getAllOBDData() onComplete")
                }
            })
    }
}
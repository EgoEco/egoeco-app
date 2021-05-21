package com.example.egoeco_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.egoeco_app.model.AppDatabase
import com.example.egoeco_app.model.DataRepository
import com.example.egoeco_app.model.OBDData
import com.example.egoeco_app.model.OBDDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ObdDataViewModel @Inject internal constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    //    private val obdDataRepository = OBDDataRepository(db)
    val obdDataList = MutableLiveData<List<OBDData>>()
    val selectedOBDData = MutableLiveData<OBDData>()
    val obdDataReceiving = MutableLiveData<Boolean>()

    init {
        getAllOBDData()
    }

    fun startReceivingOBDData() {
        obdDataReceiving.value = true
    }
    fun stopReceivingOBDData() {
        obdDataReceiving.value = false
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
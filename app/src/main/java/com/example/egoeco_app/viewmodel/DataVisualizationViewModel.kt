package com.example.egoeco_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.egoeco_app.model.DataRepository
import com.example.egoeco_app.model.OBDData
import com.example.egoeco_app.utils.DevTool.logD
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class DataVisualizationViewModel @Inject internal constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    val obdDataList = MutableLiveData<List<OBDData>>()
    private fun getAllOBDData() {
        dataRepository.getOBDDataRepository().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : Observer<List<OBDData>> {
                override fun onSubscribe(d: Disposable?) {
                    logD( "getAllOBDData() onSubscribe $d")
                }

                override fun onNext(t: List<OBDData>?) {
                    t?.let {
                        obdDataList.value = it.toList()
                    }
                }

                override fun onError(e: Throwable?) {
                    logD( "getAllOBDData() onError $e")
                }

                override fun onComplete() {
                    logD( "getAllOBDData() onComplete")
                }
            })
    }
}
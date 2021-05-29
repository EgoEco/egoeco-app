package com.example.egoeco_app.viewmodel

import android.util.Log
import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.egoeco_app.model.DataRepository
import com.example.egoeco_app.model.OBDData
import com.example.egoeco_app.utils.DevTool.logD
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ObdDataDetailViewModel @Inject internal constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    val data = MutableLiveData<OBDData>()

    fun getOBDDataById(id: Long) {
        dataRepository.getOBDDataRepository().getOBDData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : Observer<OBDData> {
                override fun onSubscribe(d: Disposable?) {
                    logD( "getOBDDataById() onSubscribe $d")
                }

                override fun onNext(t: OBDData?) {
                    logD( "getOBDDataById() onNext $t")
                    t?.let {
                        data.value = it
                    }
                }

                override fun onError(e: Throwable?) {
                    logD( "getOBDDataById() onError $e")
                }

                override fun onComplete() {
                    logD( "getOBDDataById() onComplete")
                }
            })
    }
}
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
}
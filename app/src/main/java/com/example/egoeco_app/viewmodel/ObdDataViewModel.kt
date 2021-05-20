package com.example.egoeco_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.egoeco_app.model.OBDData

class ObdDataViewModel : ViewModel() {
    val obdDataList = MutableLiveData<OBDData>()

//    fun getAllData(data: OBDData): List<OBDData> {
//    }
    fun insertData(data: OBDData) {
    }
    fun updateData(data: OBDData) {
    }
    fun deleteData(data: OBDData) {
    }
}
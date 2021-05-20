package com.example.egoeco_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.egoeco_app.model.DataRepository
import javax.inject.Inject

class MyMenuViewModel @Inject internal constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
}
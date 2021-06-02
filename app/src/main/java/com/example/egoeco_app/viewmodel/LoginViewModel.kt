package com.example.egoeco_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.egoeco_app.model.repo.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject internal constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
}
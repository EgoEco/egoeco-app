package com.example.egoeco_app.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject internal constructor(
    private val obdDataRepository: OBDDataRepository
) {
    fun getOBDDataRepository() = obdDataRepository
}
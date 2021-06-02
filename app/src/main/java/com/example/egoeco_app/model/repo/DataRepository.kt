package com.example.egoeco_app.model.repo

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject internal constructor(
    private val obdDataRepository: OBDDataRepository,
    private val userRepository: UserRepository,
) {
    fun getOBDDataRepository() = obdDataRepository
    fun getUserRepository() = userRepository
}
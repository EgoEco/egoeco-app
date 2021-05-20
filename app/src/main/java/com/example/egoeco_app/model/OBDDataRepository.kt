package com.example.egoeco_app.model

import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OBDDataRepository @Inject internal constructor(
    val obdDataDao: OBDDataDao
) {
    fun getOBDData(id: Long) = obdDataDao.getOBDData(id)
    fun getAll() = obdDataDao.getAll()
    fun insertOBDData(data: OBDData) = obdDataDao.insert(data)
    fun updateOBDData(data: OBDData) = obdDataDao.update(data)
    fun deleteOBDData(data: OBDData) = obdDataDao.delete(data)
    fun deleteOBDDataById(id: Long) = obdDataDao.deleteOBDDataById(id)
}
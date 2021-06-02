package com.example.egoeco_app.model.room

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
    fun deleteAll() = obdDataDao.deleteAll()
}
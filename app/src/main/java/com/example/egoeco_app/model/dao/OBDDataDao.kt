package com.example.egoeco_app.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.egoeco_app.model.entity.OBDData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface OBDDataDao: BaseDao<OBDData> {
    @Query("select * from obd_data")
    fun getAll(): Observable<List<OBDData>>

    @Query("select * from obd_data where id=:id")
    fun getOBDData(id: Long): Observable<OBDData>

    @Query("delete from obd_data where id=:id")
    fun deleteOBDDataById(id: Long): Completable

    @Query("delete from obd_data")
    fun deleteAll(): Completable
}
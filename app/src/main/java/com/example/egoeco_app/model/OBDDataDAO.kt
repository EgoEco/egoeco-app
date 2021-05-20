package com.example.egoeco_app.model

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface OBDDataDAO<T> : BaseDAO<T> {
    @Query("select * from obd_data")
    abstract fun getAll(): Observable<List<OBDData>>

    @Query("select * from obd_data where id=:id")
    abstract fun getOBDData(id: Long): Observable<OBDData>

    @Query("delete from obd_data where id=:id")
    abstract fun deleteOBDDataById(id: Long): Completable
}
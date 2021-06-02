package com.example.egoeco_app.model.dao

import androidx.room.*
import io.reactivex.rxjava3.core.Completable

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: T): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T): Completable

    @Delete
    fun delete(obj: T): Completable
}
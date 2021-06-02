package com.example.egoeco_app.model.room

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface UserDataDao: BaseDao<User> {
    @Query("select * from user")
    fun getAll(): Observable<List<User>>

    @Query("select * from user where id=:id")
    fun getUser(id: Long): Observable<User>

    @Query("delete from user where id=:id")
    fun deleteUserById(id: Long): Completable

    @Query("delete from user")
    fun deleteAll(): Completable
}

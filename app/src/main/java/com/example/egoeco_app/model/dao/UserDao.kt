package com.example.egoeco_app.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.egoeco_app.model.entity.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface UserDao: BaseDao<User> {
    @Query("select * from user")
    fun getAll(): Observable<List<User>>

    @Query("select * from user where id=:id")
    fun getUser(id: Long): Observable<User>

    @Query("delete from user where id=:id")
    fun deleteUserById(id: Long): Completable

    @Query("delete from user")
    fun deleteAll(): Completable
}

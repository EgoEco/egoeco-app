package com.example.egoeco_app.model.room

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject internal constructor(
    val userDataDao: UserDataDao
) {
    fun getOBDData(id: Long) = userDataDao.getUser(id)
    fun getAll() = userDataDao.getAll()
    fun insertUser(user: User) = userDataDao.insert(user)
    fun updateUser(user: User) = userDataDao.update(user)
    fun deleteUser(user: User) = userDataDao.delete(user)
    fun deleteUserById(id: Long) = userDataDao.deleteUserById(id)
    fun deleteAll() = userDataDao.deleteAll()
}

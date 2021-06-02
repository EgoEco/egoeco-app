package com.example.egoeco_app.model.repo

import com.example.egoeco_app.model.dao.UserDao
import com.example.egoeco_app.model.entity.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject internal constructor(
    val userDao: UserDao,
) {
    fun getUser(id: Long) = userDao.getUser(id)
    fun getAll() = userDao.getAll()
    fun insertUser(user: User) = userDao.insert(user)
    fun updateUser(user: User) = userDao.update(user)
    fun deleteUser(user: User) = userDao.delete(user)
    fun deleteUserById(id: Long) = userDao.deleteUserById(id)
    fun deleteAll() = userDao.deleteAll()
}

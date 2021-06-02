package com.example.egoeco_app.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.egoeco_app.model.entity.OBDData
import com.example.egoeco_app.model.entity.User
import com.example.egoeco_app.model.dao.OBDDataDao

@Database(entities = [OBDData::class, User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun obdDataDao(): OBDDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "egoeco_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE as AppDatabase
        }
    }
}
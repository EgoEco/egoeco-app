package com.example.egoeco_app.di

import android.content.Context
import com.example.egoeco_app.model.room.AppDatabase
import com.example.egoeco_app.model.room.OBDDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context.applicationContext)
    }

    @Singleton
    @Provides
    fun provideOBDDataDao(appDatabase: AppDatabase): OBDDataDao {
        return appDatabase.obdDataDao()
    }
}
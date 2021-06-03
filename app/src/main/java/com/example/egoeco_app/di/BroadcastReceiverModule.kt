package com.example.egoeco_app.di

import android.content.Context
import com.example.egoeco_app.model.AppDatabase
import com.example.egoeco_app.model.bluetooth.BluetoothBroadcastReceiver
import com.example.egoeco_app.model.dao.OBDDataDao
import com.example.egoeco_app.model.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BroadcastReceiverModule {
    @Singleton
    @Provides
    fun provideBluetoothBroadcastReceiver() = BluetoothBroadcastReceiver()
}
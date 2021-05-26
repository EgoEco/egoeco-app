package com.example.egoeco_app.model

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.egoeco_app.R
import com.example.egoeco_app.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import splitties.resources.int
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothService @Inject internal constructor(
    private val dataRepository: DataRepository
) : Service() {
    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notification: Notification

    companion object {
        const val ACTION_START = "com.example.egoeco_app.START"
        const val ACTION_RUN = "com.example.egoeco_app.RUN"
        const val ACTION_STOP = "com.example.egoeco_app.STOP"
        const val CHANNEL_ID = "EgoEcoApp Channel"
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Needless")
    }

    override fun onCreate() {
        Log.d("KHJ", "Bluetooth Service onCreate()")
        pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("KHJ", "Bluetooth Service onStartCommand() action: $action")
        when (action) {
            ACTION_START -> {
                notificationChannel = createNotificationChannel()
                notification = createNotification()
                startForeground(1, notification)
//                val thread = ServiceThread1()
//                thread.start()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("KHJ", "BluetoothService onDestroy()")
        super.onDestroy()
    }

    private fun createNotificationChannel(): NotificationChannel {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "EgoEco App",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "This is EgoEco Application"
//            setShowBadge(true)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
        return serviceChannel
    }

    private fun createNotification(contentText: String = "연결됐어요옹"): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EgoEco Foreground Service")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setTicker("hello")
            .setSmallIcon(R.drawable.egoeco_app_logo)
            .build()
    }

    fun insertOBDData(data: OBDData) {
        dataRepository.getOBDDataRepository().insertOBDData(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CompletableObserver {
                override fun onSubscribe(d: Disposable?) {
                    Log.d("KHJ", "BluetoothService insertOBDData() onSubscribe $d")
                }

                override fun onComplete() {
                    Log.d("KHJ", "BluetoothService insertOBDData() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "BluetoothService insertOBDData() onError $e")
                }
            })
    }
}
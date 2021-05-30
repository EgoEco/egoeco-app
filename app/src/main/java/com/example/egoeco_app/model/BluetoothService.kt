package com.example.egoeco_app.model

import android.app.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.egoeco_app.R
import com.example.egoeco_app.utils.DevTool.logD
import com.example.egoeco_app.utils.DevTool.logE
import com.example.egoeco_app.view.MainActivity
import com.github.zakaprov.rxbluetoothadapter.ConnectionState
import com.github.zakaprov.rxbluetoothadapter.RxBluetoothAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.IOException
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothService : Service() {
    @Inject
    lateinit var dataRepository: DataRepository // 다른 것처럼 internal constructor로 하면 에러뜸. 서비스는 argument없이 하는게 최고..

    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notification: Notification
    var serviceState = false

    val mCompositeDisposable = CompositeDisposable()

    val connectionState = MutableLiveData<Int>(-1)
    val scanningState = MutableLiveData<Int>(-1)
    val pairable = MutableLiveData<Boolean>(false)
    private val adapter by lazy { RxBluetoothAdapter(application) }
    private var mDevice: BluetoothDevice? = null
    private var mSocket: BluetoothSocket? = null
    private val bluetoothDeviceList = mutableListOf<BluetoothDevice>()

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
        logD("Bluetooth Service onCreate()")
        pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        super.onCreate()
    }

    @ExperimentalUnsignedTypes
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        logD("Bluetooth Service onStartCommand() action: $action")
        when (action) {
            ACTION_START -> {
                if (!serviceState) {
                    serviceState = true
                    notificationChannel = createNotificationChannel()
                    notification = createNotification("블루투스 스캔중..")
                    startForeground(1, notification)
                    startBluetooth()
                }
            }
        }
//        return super.onStartCommand(intent, flags, startId)
        return START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        logD("BluetoothService onDestroy()")
        serviceState = false
        sendBluetoothBroadcast("connect", -1)
        disconnect()
        mCompositeDisposable.dispose()
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

    private fun createNotification(contentText: String = "블루투스 연결중"): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EgoEco")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.egoeco_app_logo)
            .build()
    }

    private fun startBluetooth() {
        logD("Start Bluetooth")
        initialize()
        scan()
//        pair()
//        connect()
    }

    private fun initialize() {
        logD("Start Initializing Sequence")
        val connectionDisposable = RxBluetoothAdapter(applicationContext).connectionEventStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ (state, device) ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        logD("${device.address} - connected")
                        notification = createNotification("블루투스 연결됨")
                        startForeground(1, notification)
                        connectionState.value = 1
                        sendBluetoothBroadcast("connect", 1)
                    }
                    ConnectionState.CONNECTING -> {
                        logD("${device.address} - connecting")
                        notification = createNotification("블루투스 연결중")
                        startForeground(1, notification)
                        connectionState.value = 0
                        sendBluetoothBroadcast("connect", 0)
                    }
                    ConnectionState.DISCONNECTED -> {
                        logD("${device.address} - disconnected")
                        connectionState.value = -1
                        notification = createNotification("블루투스 연결 끊김")
                        startForeground(1, notification)
                        logD("sendingBluetoothBroadcast connect: -1")
                        sendBluetoothBroadcast("connect", -1)
                        disconnect()
                        stopSelf()
                    }
                }
            }) { error ->
                logE("error in connectionState $error")
                sendBluetoothBroadcast("connect", -1)
                disconnect()
                connectionState.value = -1
                stopSelf()
            }
        mCompositeDisposable.add(connectionDisposable)

        val scanningDisposable = adapter.scanStateStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ isScanning ->
                logD("isScanning: $isScanning")
            }, { error ->
                logD("error in scanStateStream: $error")
                sendBluetoothBroadcast("scan", -1)
                disconnect()
                stopSelf()
            }, {
                logD("scanStateStream onComplete()")
            })
        mCompositeDisposable.add(scanningDisposable)
    }

    private fun sendBluetoothBroadcast(name: String, value: Int) {
        val localIntent = Intent("bluetoothServiceIntent")
            .apply { putExtra(name, value) }
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(localIntent)
    }

    private fun scan() {
        logD("Start Scanning Sequence")
        val scanningDisposable = adapter.startDeviceScan()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ device ->
                // Process next discovered device
                logD(
                    "device: ${device.address} ${device.name} ${device.type} ${device.bondState}"
                )
                scanningState.value = 0
                sendBluetoothBroadcast("scan", 0)
                if (device !in bluetoothDeviceList) bluetoothDeviceList.add(device)
                if (pairable.value == false && device.name == "HC-06") {
                    pairable.value = true
//                    pair()
                }
            }, { error ->
                logE("in scan() error: $error")
                scanningState.value = -1
                sendBluetoothBroadcast("scan", -1)
                disconnect()
                stopSelf()
                // Handle error
            }, {
                logD("Scan Complete.")
                scanningState.value = 1
                sendBluetoothBroadcast("scan", 1)
                pair()
                // Scan complete
            })
        mCompositeDisposable.add(scanningDisposable)
    }

    private fun pair() {
        logD("Start Pairing Sequence")
        logD("bluetoothDeviceList: $bluetoothDeviceList")
        mDevice = bluetoothDeviceList.find { it.name == "HC-06" }
        val device = mDevice
        if (device != null) {
            logD("pairing with HC-06...")
            val pairingDisposable = adapter.pairDevice(device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    // Process pairing result
                    logD("pairing result: $result")
                    if (result) {
                        sendBluetoothBroadcast("pair", 1)
                        connect()
                    }
                }, { error ->
                    // Handle error
                    logE("error in pairing: $error")
                    sendBluetoothBroadcast("pair", -1)
                    disconnect()
                    stopSelf()
                })
            mCompositeDisposable.add(pairingDisposable)
        } else {
            logE("in pair() device is null")
            sendBluetoothBroadcast("pair", -1)
            stopSelf()
        }
    }

    @ExperimentalUnsignedTypes
    fun makeOBDDataFromByteArray(byteList: List<UByte>): OBDData? {
        val obdData = OBDData().apply {
            prefix1 = byteList[0].toInt()
            prefix2 = byteList[1].toInt()
            engRPM_A = byteList[2].toInt()
            engRPM_B = byteList[3].toInt()
            vehicleSpd = byteList[4].toInt()
            ecoDriveLevel = byteList[5].toInt()
            reserved = byteList[6].toInt()
            checkSum = byteList[7].toInt()
            timeStamp = System.currentTimeMillis()
            initialize()
        }
        return if (obdData.validate()) obdData else null
    }


    fun connect() {
        logD("Start Connecting Sequence")
        val device = mDevice
        if (device != null) {
            val connectDisposable = adapter.connectToDevice(device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ socket ->
                    // Connection successful, save and/or use the obtained socket object
                    logD("Connection Successful. socket: $socket")
                    mSocket = socket
                    val subject = PublishSubject.create<ByteArray>()
                    val subjectDisposable = subject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ byteArray ->
                            val byteList = byteArray.take(8).map { it.toUByte() }
                            val data = makeOBDDataFromByteArray(byteList)
                            logD("$byteList")
                            if (data != null) insertOBDData(data)
                            else logD("data is null! maybe failed in validation")
                        }) { error ->
                            logE("Error in byteArraySubject: $error")
                            logE("Stopping Service!")
                            disconnect()
                            stopSelf()
                        }
                    mCompositeDisposable.add(subjectDisposable)
                    ConnectedBluetoothThread(socket, subject).start()
/*
                    // Studying how to observe socket's InputStream in real-time
                    PublishSubject.fromArray(socket.inputStream)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            val buffer = ByteArray(8)
                            val bytes = socket.inputStream.available()
                            it.read(buffer, 0, bytes)
                            logD( "socket.inputStream buffer: ${buffer.toList()}")
                        }, { error ->
                            logE( "error in socket.inputstream observable: $error")
                        }, {
                            logD( "socket.inputStream Completed")
                        })
*/
                }, { error ->
                    // Connection failed, handle the error
                    logE("Connection failed. error: $error")
                    disconnect()
                    sendBluetoothBroadcast("connect", -1)
                    stopSelf()
                })
            mCompositeDisposable.add(connectDisposable)
        } else {
            logE("in connect() device is null")
        }
    }

    class ConnectedBluetoothThread(
        private val socket: BluetoothSocket,
        private val subject: PublishSubject<ByteArray>
    ) : Thread() {
        val inputStream by lazy { socket.inputStream }
        val outputStream by lazy { socket.outputStream }
        override fun run() {
            val buffer = ByteArray(24) // 최소한 8 이상. 딱 8은 ArrayIndexBound 예외발생위험.
            var bytes: Int
            while (true) {
                try {
                    bytes = inputStream.available()
                    if (bytes != 0) {
//                        SystemClock.sleep(500) // 500으로 하면 데이터가 8이 아니라 16개가 들어옴.
                        SystemClock.sleep(400)
                        bytes = inputStream.available()
//                        logD("available bytes: $bytes")
                        bytes = inputStream.read(buffer, 0, bytes)
//                        logD("read bytes: $bytes")
//                        val sdf = SimpleDateFormat("hh:mm:ss:SS")
//                        logD( "time: ${sdf.format(System.currentTimeMillis())}")
                        subject.onNext(buffer)
                    }
                } catch (e: IOException) {
                    logE("in ConnectedBluetoothThread $e")
                    disconnectSocket()
                    subject.onError(e)
                    break
                } catch (e: ArrayIndexOutOfBoundsException) {
                    logE("in ConnectedBluetoothThread $e")
                    disconnectSocket()
                    subject.onError(e)
                    break
                } catch (e: NullPointerException) {
                    logE("in ConnectedBluetoothThread $e")
                    disconnectSocket()
                    subject.onError(e)
                    break
                } catch (e: Exception) {
                    logE("in ConnectedBluetoothThread $e")
                    disconnectSocket()
                    subject.onError(e)
                    break
                }
            }
        }

        private fun disconnectSocket() {
            inputStream?.close()
            outputStream?.close()
            socket.close()
        }
    }

    private fun disconnect() {
        mSocket?.outputStream?.close()
        mSocket?.inputStream?.close()
        mSocket?.close()
    }

    private fun insertOBDData(data: OBDData) {
        dataRepository.getOBDDataRepository().insertOBDData(data)
            .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
            .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
            .subscribeWith(object : CompletableObserver {
                override fun onSubscribe(d: Disposable?) {
//                    logD( "BluetoothService insertOBDData() onSubscribe $d")
                }

                override fun onComplete() {
//                    logD( "BluetoothService insertOBDData() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    logD("BluetoothService insertOBDData() onError $e")
                }
            })
    }
}
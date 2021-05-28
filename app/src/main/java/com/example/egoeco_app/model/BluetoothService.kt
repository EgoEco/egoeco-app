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
        Log.d("KHJ", "Bluetooth Service onCreate()")
        pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        super.onCreate()
    }

    @ExperimentalUnsignedTypes
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("KHJ", "Bluetooth Service onStartCommand() action: $action")
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
        Log.d("KHJ", "BluetoothService onDestroy()")
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
        Log.d("KHJ", "Start Bluetooth")
        initialize()
        scan()
//        pair()
//        connect()
    }

    private fun initialize() {
        Log.d("KHJ", "Start Initializing Sequence")
        val connectionDisposable = RxBluetoothAdapter(applicationContext).connectionEventStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ (state, device) ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        Log.d("KHJ", "${device.address} - connected")
                        notification = createNotification("블루투스 연결됨")
                        startForeground(1, notification)
                        connectionState.value = 1
                        sendBluetoothBroadcast("connect", 1)
                    }
                    ConnectionState.CONNECTING -> {
                        Log.d("KHJ", "${device.address} - connecting")
                        notification = createNotification("블루투스 연결중")
                        startForeground(1, notification)
                        connectionState.value = 0
                        sendBluetoothBroadcast("connect", 0)
                    }
                    ConnectionState.DISCONNECTED -> {
                        Log.d("KHJ", "${device.address} - disconnected")
                        connectionState.value = -1
                        notification = createNotification("블루투스 연결 끊김")
                        startForeground(1, notification)
                        Log.d("KHJ", "sendingBluetoothBroadcast connect: -1")
                        sendBluetoothBroadcast("connect", -1)
                        disconnect()
                        stopSelf()
                    }
                }
            }) { error ->
                Log.e("KHJ", "error in connectionState $error")
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
                Log.d("KHJ", "isScanning: $isScanning")
            }, { error ->
                Log.d("KHJ", "error in scanStateStream: $error")
                sendBluetoothBroadcast("scan", -1)
                disconnect()
                stopSelf()
            }, {
                Log.d("KHJ", "scanStateStream onComplete()")
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
        Log.d("KHJ", "Start Scanning Sequence")
        val scanningDisposable = adapter.startDeviceScan()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ device ->
                // Process next discovered device
                Log.d(
                    "KHJ",
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
                Log.e("KHJ", "in scan() error: $error")
                scanningState.value = -1
                sendBluetoothBroadcast("scan", -1)
                disconnect()
                stopSelf()
                // Handle error
            }, {
                Log.d("KHJ", "Scan Complete.")
                scanningState.value = 1
                sendBluetoothBroadcast("scan", 1)
                pair()
                // Scan complete
            })
        mCompositeDisposable.add(scanningDisposable)
    }

    private fun pair() {
        Log.d("KHJ", "Start Pairing Sequence")
        Log.d("KHJ", "bluetoothDeviceList: $bluetoothDeviceList")
        mDevice = bluetoothDeviceList.find { it.name == "HC-06" }
        val device = mDevice
        if (device != null) {
            Log.d("KHJ", "pairing with HC-06...")
            val pairingDisposable = adapter.pairDevice(device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    // Process pairing result
                    Log.d("KHJ", "pairing result: $result")
                    if (result) {
                        sendBluetoothBroadcast("pair", 1)
                        connect()
                    }
                }, { error ->
                    // Handle error
                    Log.e("KHJ", "error in pairing: $error")
                    sendBluetoothBroadcast("pair", -1)
                    disconnect()
                    stopSelf()
                })
            mCompositeDisposable.add(pairingDisposable)
        } else {
            Log.e("KHJ", "in pair() device is null")
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
        Log.d("KHJ", "Start Connecting Sequence")
        val device = mDevice
        if (device != null) {
            val connectDisposable = adapter.connectToDevice(device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ socket ->
                    // Connection successful, save and/or use the obtained socket object
                    Log.d("KHJ", "Connection Successful. socket: $socket")
                    mSocket = socket
                    val subject = PublishSubject.create<ByteArray>()
                    val subjectDisposable = subject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ byteArray ->
                            val byteList = byteArray.take(8).map { it.toUByte() }
                            val data = makeOBDDataFromByteArray(byteList)
                            Log.d("KHJ", "$byteList")
                            if (data != null) insertOBDData(data)
                            else Log.d("KHJ", "data is null! maybe failed in validation")
                        }) { error ->
                            Log.e("KHJ", "Error in byteArraySubject: $error")
                            Log.e("KHJ", "Stopping Service!")
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
                            Log.d("KHJ", "socket.inputStream buffer: ${buffer.toList()}")
                        }, { error ->
                            Log.e("KHJ", "error in socket.inputstream observable: $error")
                        }, {
                            Log.d("KHJ", "socket.inputStream Completed")
                        })
*/
                }, { error ->
                    // Connection failed, handle the error
                    Log.e("KHJ", "Connection failed. error: $error")
                    disconnect()
                    sendBluetoothBroadcast("connect", -1)
                    stopSelf()
                })
            mCompositeDisposable.add(connectDisposable)
        } else {
            Log.e("KHJ", "in connect() device is null")
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
//                        Log.d("KHJ","available bytes: $bytes")
                        bytes = inputStream.read(buffer, 0, bytes)
//                        Log.d("KHJ","read bytes: $bytes")
//                        val sdf = SimpleDateFormat("hh:mm:ss:SS")
//                        Log.d("KHJ", "time: ${sdf.format(System.currentTimeMillis())}")
                        subject.onNext(buffer)
                    }
                } catch (e: IOException) {
                    Log.e("KHJ", "in ConnectedBluetoothThread $e")
                    disconnectSocket()
                    subject.onError(e)
                    break
                } catch (e: ArrayIndexOutOfBoundsException) {
                    Log.e("KHJ", "in ConnectedBluetoothThread $e")
                    disconnectSocket()
                    subject.onError(e)
                    break
                } catch (e: NullPointerException) {
                    Log.e("KHJ", "in ConnectedBluetoothThread $e")
                    disconnectSocket()
                    subject.onError(e)
                    break
                } catch (e: Exception) {
                    Log.e("KHJ", "in ConnectedBluetoothThread $e")
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
//                    Log.d("KHJ", "BluetoothService insertOBDData() onSubscribe $d")
                }

                override fun onComplete() {
//                    Log.d("KHJ", "BluetoothService insertOBDData() onComplete ")
                }

                override fun onError(e: Throwable?) {
                    Log.d("KHJ", "BluetoothService insertOBDData() onError $e")
                }
            })
    }
}
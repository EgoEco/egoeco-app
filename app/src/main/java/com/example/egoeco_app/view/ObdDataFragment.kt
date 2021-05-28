package com.example.egoeco_app.view

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.egoeco_app.R
import com.example.egoeco_app.adapter.OBDListAdapter
import com.example.egoeco_app.databinding.FragmentObdDataBinding
import com.example.egoeco_app.model.BluetoothState
import com.example.egoeco_app.model.OBDData
import com.example.egoeco_app.viewmodel.MainViewModel
import com.example.egoeco_app.viewmodel.ObdDataViewModel
import com.trello.rxlifecycle4.android.ActivityEvent
import com.trello.rxlifecycle4.android.FragmentEvent
import com.trello.rxlifecycle4.components.support.RxFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.subscribers.SubscriberResourceWrapper
import io.reactivex.rxjava3.schedulers.Schedulers
import splitties.toast.toast
import java.util.*
import java.util.concurrent.Flow
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@AndroidEntryPoint
class ObdDataFragment : RxFragment() {
    private val binding by lazy { FragmentObdDataBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by activityViewModels()
    private val adapter: OBDListAdapter by lazy { OBDListAdapter() }
    private var commState = false
    private var lastScanState = -1
    private var lastPairState = -1
    private var lastConnectState = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding.apply {
            viewModel = this@ObdDataFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            obdRecyclerView.adapter = adapter
            obdRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            bluetotohProgressBar.visibility = View.GONE
            startButton.setOnClickListener {
                if (checkLocationPermission()) {
                    when (commState) {
                        true -> {
                            stopService()
                        }
                        else -> {
                            startService()
                        }
                    }
                } else {
                    toast("권한을 승인해야합니다.")
                    requestLocationPermission()
                    if (!checkLocationPermission()) {
                        showPermissionAlert()
                    }
                }
            }
            bluetoothRandomButton.setOnClickListener {
                insertRandomData()
            }
            bluetoothDataRemoveButton.setOnClickListener {
                deleteAllOBDData()
            }
        }

/*
        viewModel.obdDataReceiving.observe(viewLifecycleOwner) {
            receivingDataState = it
            toast("receivingDataState: $receivingDataState")
            when (it) {
                true -> binding.startButton.setImageResource(R.drawable.ic_baseline_stop_circle_24)
                false -> binding.startButton.setImageResource(R.drawable.ic_baseline_not_started_24)
            }
        }
*/

        viewModel.obdDataList.observe(viewLifecycleOwner) {
            Log.d("KHJ", "obdDataList.observe, task: $it")
            adapter.submitList(it)
        }
        adapter.setOBDListAdapterListener(object : OBDListAdapter.OBDListAdapterListener {
            override fun onClicked(data: OBDData) {
//                toast("onClicked OBDList")
                val dialog = ObdDataDetailFragment(data.id)
                dialog.show(childFragmentManager, "ObdDataDetailFragment")
            }
        })

//        lastScanState = viewModel.scanState.value!!
//        lastPairState = viewModel.pairState.value!!
//        lastConnectState = viewModel.connectState.value!

        viewModel.bluetoothState.observe(viewLifecycleOwner) { (action, state) ->
            Log.d("KHJ", "action: $action, state: $state")
            when (action) {
                BluetoothState.SCAN -> {
                    if (lastScanState == state) return@observe
                    when (state) {
                        -1 -> {
                            stopService()
                            toast("Scanning Failed.")
                        }
                        0 -> {
                            toast("Scanning..")
                            inService(BluetoothState.SCAN)
                        }
                        1 -> {
                            toast("Scanning Completed!")
                            inService(BluetoothState.SCAN)
                        }
                    }
                    lastScanState = state
                }
                BluetoothState.PAIR -> {
                    if (lastPairState == state) return@observe
                    when (state) {
                        -1 -> {
                            stopService()
                            toast("Pairing Failed.")
                        }
                        1 -> {
                            toast("Pairing Completed!")
                            inService(BluetoothState.PAIR)
                            binding.startButton.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24)
                        }
                    }
                    lastPairState = state
                }
                BluetoothState.CONNECT -> {
                    if (lastConnectState == state) {
                        if (state == -1) outOfService()
                        toast("Not Connected")
                        return@observe
                    }
                    when (state) {
                        -1 -> {
                            stopService()
                            toast("Connecting Failed.")
                        }
                        0 -> {
                            toast("Connecting..")
                            inService(BluetoothState.CONNECT)
                            binding.startButton.setImageResource(R.drawable.ic_baseline_bluetooth_drive_24)
                        }
                        1 -> {
                            toast("Connecting Established!")
                            commState = true
                            inService(BluetoothState.CONNECT)
                            binding.startButton.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24)
                            binding.startButton.isEnabled = true
                            binding.bluetotohProgressBar.visibility = View.GONE
                        }
                    }
                    lastConnectState = state
                }
            }
        }
        return binding.root
    }

    @ExperimentalUnsignedTypes
    fun insertRandomData() {
        val data = OBDData().apply {
            prefix1 = "0x55".removePrefix("0x").toInt(16)
            prefix2 = "0x01".removePrefix("0x").toInt(16)
            engRPM_A = "0x05".removePrefix("0x").toInt(16) + Random.nextInt(50)
            engRPM_B = "0x05".removePrefix("0x").toInt(16) + Random.nextInt(50)
            vehicleSpd = "0x10".removePrefix("0x").toInt(16) + Random.nextInt(35)
            reserved = 0
            ecoDriveLevel = "0x01".removePrefix("0x").toInt(16) + Random.nextInt(5)
            timeStamp = System.currentTimeMillis()
            initialize()
            validate()
        }
        viewModel.insertOBDData(data)
    }

    private fun checkLocationPermission(): Boolean {
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        Log.d("KHJ", "coarseLocationPermission: $coarseLocationPermission")
        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            MainViewModel.LOCATION_PERMISSION_CODE
        )
    }

    fun showPermissionAlert() {
        AlertDialog.Builder(requireContext()).setTitle("권한 설정")
            .setMessage("권한 거절로 인해 일부기능이 제한됩니다.")
            .setPositiveButton(
                "권한 설정"
            ) { dialog, which ->
                try {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + requireContext().packageName));
                    startActivity(intent);
                } catch (e: ActivityNotFoundException) {
                    Log.e("KHJ", "error in permission activity $e")
                    e.printStackTrace();
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            }
            .create()
            .show();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MainViewModel.LOCATION_PERMISSION_CODE -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        toast("RESULT_OK")
                    }
                    AppCompatActivity.RESULT_CANCELED -> {
                        toast("RESULT_CANCELED")
                    }
                    else -> {
                        toast("RESULT_UNKNOWN")
                    }
                }
            }
            else -> {
                toast("Unknown requestCode")
            }
        }
    }

    private fun deleteAllOBDData() {
        viewModel.deleteAllOBDData()
    }

    private fun startService() {
//        toast("startService")
        viewModel.startService()
        inService(BluetoothState.SCAN)
    }

    private fun stopService() {
//        toast("stopService")
        viewModel.stopService()
        outOfService()
    }

    private fun inService(state: BluetoothState) {
        commState = true
        when (state) {
            BluetoothState.SCAN -> {
                binding.startButton.setImageResource(R.drawable.ic_baseline_bluetooth_searching_24)
                binding.bluetotohProgressBar.visibility = View.VISIBLE
                binding.startButton.isEnabled = false
            }
            BluetoothState.PAIR -> {
                binding.startButton.setImageResource(R.drawable.ic_baseline_bluetooth_connected_24)
                binding.bluetotohProgressBar.visibility = View.VISIBLE
                binding.startButton.isEnabled = false
            }
            BluetoothState.CONNECT -> {
                binding.startButton.setImageResource(R.drawable.ic_baseline_bluetooth_drive_24)
                binding.bluetotohProgressBar.visibility = View.VISIBLE
                binding.startButton.isEnabled = false
            }
        }
    }

    private fun outOfService() {
        commState = false
        binding.startButton.isEnabled = true
        binding.bluetotohProgressBar.visibility = View.GONE
        binding.startButton.setImageResource(R.drawable.ic_baseline_bluetooth_24)
    }
}
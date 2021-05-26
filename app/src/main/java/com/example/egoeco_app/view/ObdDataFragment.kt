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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.egoeco_app.R
import com.example.egoeco_app.adapter.OBDListAdapter
import com.example.egoeco_app.databinding.FragmentObdDataBinding
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
    private var receivingDataState = false
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
            startButton.setOnClickListener {
                if (checkLocationPermission()) {
                    when (receivingDataState) {
                        true -> {
                            this@ObdDataFragment.viewModel.stopReceivingOBDData()
                        }
                        false -> {
                            this@ObdDataFragment.viewModel.startReceivingOBDData()
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
        }
        receivingDataState = viewModel.obdDataReceiving.value ?: false
        var intervalSubscription: Disposable = Observable.empty<Unit>().subscribe()
        intervalSubscription.dispose()
        viewModel.obdDataReceiving.observe(viewLifecycleOwner) {
            receivingDataState = it
            toast("receivingDataState: $receivingDataState")
            when (receivingDataState) {
                true -> {
                    intervalSubscription = Observable.interval(500L, 500L, TimeUnit.MILLISECONDS)
//                        .takeWhile { receivingDataState }
                        .subscribeOn(Schedulers.io())
                        .subscribe { insertRandomData() }
                    binding.startButton.setImageResource(R.drawable.ic_baseline_stop_circle_24)
                }
                false -> {
                    intervalSubscription.dispose()
                    binding.startButton.setImageResource(R.drawable.ic_baseline_not_started_24)
                }
            }
        }

//        viewModel.
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
        return binding.root
    }

    fun insertRandomData() {
        val data = OBDData()
        data.prefix1 = "0x55".removePrefix("0x").toInt(16)
        data.prefix2 = "0x01".removePrefix("0x").toInt(16)
        data.engRPM_A = "0x05".removePrefix("0x").toInt(16) + Random.nextInt(50)
        data.engRPM_B = "0x05".removePrefix("0x").toInt(16) + Random.nextInt(50)
        data.vehicleSpd = "0x10".removePrefix("0x").toInt(16) + Random.nextInt(35)
        data.ecoDriveLevel = "0x01".removePrefix("0x").toInt(16) + Random.nextInt(5)
        data.timeStamp = System.currentTimeMillis()
        data.initCheckSum()
        data.initRPM()
        data.initTimeString()
        viewModel.insertOBDData(data)
    }

    fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        Log.d("KHJ","fineLocationPermission: $fineLocationPermission")
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        Log.d("KHJ","coarseLocationPermission: $coarseLocationPermission")
//        return fineLocationPermission == PackageManager.PERMISSION_GRANTED
//                && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
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
}
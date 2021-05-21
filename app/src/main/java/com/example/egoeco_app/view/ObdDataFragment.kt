package com.example.egoeco_app.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.egoeco_app.R
import com.example.egoeco_app.adapter.OBDListAdapter
import com.example.egoeco_app.databinding.FragmentObdDataBinding
import com.example.egoeco_app.model.OBDData
import com.example.egoeco_app.viewmodel.ObdDataViewModel
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
    private val viewModel: ObdDataViewModel by viewModels()
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
                when (receivingDataState) {
                    true -> {
                        this@ObdDataFragment.viewModel.stopReceivingOBDData()
                    }
                    false -> {
                        this@ObdDataFragment.viewModel.startReceivingOBDData()
                    }
                }
            }
        }
        var intervalSubscription: Disposable = Observable.empty<Unit>().subscribe()
        intervalSubscription.dispose()
        viewModel.obdDataReceiving.observe(viewLifecycleOwner) {
            receivingDataState = it
            when (it) {
                true -> {
                    intervalSubscription = Observable.interval(500L, 500L, TimeUnit.MILLISECONDS)
                        .takeWhile { receivingDataState }
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
        data.engRPM_A = "0x00".removePrefix("0x").toInt(16) + Random.nextInt(50)
        data.engRPM_B = "0x00".removePrefix("0x").toInt(16) + Random.nextInt(50)
        data.vehicleSpd = "0x00".removePrefix("0x").toInt(16) + Random.nextInt(50)
        data.ecoDriveLevel = "0x03".removePrefix("0x").toInt(16)
        data.checkSum = "0x55".removePrefix("0x").toInt(16)
        data.timeStamp = System.currentTimeMillis()
        data.initCheckSum()
        data.initRPM()
        data.initTimeString()
        viewModel.insertOBDData(data)
    }
}
package com.example.egoeco_app.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.FragmentDataVisualizationBinding
import com.example.egoeco_app.viewmodel.DataVisualizationViewModel
import com.example.egoeco_app.viewmodel.MainViewModel
import com.example.egoeco_app.viewmodel.MyMenuViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.trello.rxlifecycle4.components.support.RxFragment
import dagger.hilt.android.AndroidEntryPoint
import splitties.views.backgroundColor
import java.text.SimpleDateFormat

@AndroidEntryPoint
class DataVisualizationFragment : RxFragment() {
    private val binding by lazy { FragmentDataVisualizationBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding.apply {
            viewModel = this@DataVisualizationFragment.viewModel
            lifecycleOwner = this@DataVisualizationFragment
        }
        var entryList: List<Entry>
        var colorList: List<Int>
        val sdf = SimpleDateFormat("ss.SS")
        viewModel.obdDataList.observe(viewLifecycleOwner) {
            entryList = it.takeLast(12).map { data ->
                Entry(sdf.format(data.timeStamp).toFloat(), data.rpm.toFloat())
            }
            colorList = it.takeLast(12).map { data ->
                when (data.ecoDriveLevel) {
                    in 1..2 -> Color.RED
                    in 3..5 -> Color.GREEN
                    else -> Color.YELLOW
                }
            }
            if (entryList.isNotEmpty()) {
                Log.d("KHJ", "entryList: $entryList")
                val dataset = LineDataSet(entryList.sortedBy { entry -> entry.x }, "RPM")
//                dataset.colors = ColorTemplate.COLORFUL_COLORS.toList()
                dataset.colors = colorList
                val data = LineData(dataset)
                binding.apply {
                    chart.data = data
                    chart.notifyDataSetChanged()
                    chart.invalidate()
                    val lastData = it.last()
                    visualTimeTextView.text = lastData.timeString
                    visualRPMTextView.text = lastData.rpm.toString()
                    visualSpdTextView.text = lastData.vehicleSpd.toString()
                    visualEcoLvTextView.text = lastData.ecoDriveLevel.toString()
                    visualEcoLvTextView.setTextColor(
                        when (lastData.ecoDriveLevel) {
                            1 -> Color.RED
                            2 -> Color.YELLOW
                            3 -> Color.GRAY
                            4 -> Color.BLUE
                            5 -> Color.GREEN
                            else -> Color.DKGRAY
                        }
                    )
                }
            }
        }
        binding.chart.apply {
            animateY(1000)
            description.text = "Desc"
            minimumWidth = 60
            axisLeft.textColor = Color.WHITE
            axisRight.textColor = Color.WHITE
            xAxis.textColor = Color.WHITE
            legend.textColor = Color.WHITE
            description.textColor = Color.WHITE
        }
        return binding.root
    }
}
package com.example.egoeco_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.egoeco_app.R
import com.example.egoeco_app.adapter.OBDListAdapter
import com.example.egoeco_app.databinding.FragmentObdDataBinding
import com.example.egoeco_app.model.OBDData
import com.example.egoeco_app.viewmodel.ObdDataViewModel
import com.trello.rxlifecycle4.components.support.RxFragment
import splitties.toast.toast

class ObdDataFragment : RxFragment() {
    private val viewModel: ObdDataViewModel by viewModels()
    private val binding by lazy { FragmentObdDataBinding.inflate(layoutInflater) }
    private val adapter: OBDListAdapter by lazy { OBDListAdapter() }
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
        }
//        viewModel.
        adapter.setOBDListAdapterListener(object : OBDListAdapter.OBDListAdapterListener {
            override fun onClicked(data: OBDData) {
                toast("onClicked OBDList")
            }
        })
        return binding.root
    }
}
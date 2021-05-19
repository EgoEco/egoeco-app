package com.example.egoeco_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.FragmentObdDataBinding
import com.example.egoeco_app.viewmodel.ObdDataViewModel
import com.trello.rxlifecycle4.components.support.RxFragment

class ObdDataFragment : RxFragment() {
    private val viewModel: ObdDataViewModel by viewModels()
    private val binding by lazy { FragmentObdDataBinding.inflate(layoutInflater) }
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
        }
        return binding.root
    }
}
package com.example.egoeco_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.FragmentMyMenuBinding
import com.example.egoeco_app.viewmodel.MainViewModel
import com.example.egoeco_app.viewmodel.MyMenuViewModel
import com.trello.rxlifecycle4.components.support.RxFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMenuFragment : RxFragment() {
    private val binding by lazy { FragmentMyMenuBinding.inflate(layoutInflater) }
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
            viewModel = this@MyMenuFragment.viewModel
            lifecycleOwner = this@MyMenuFragment
        }
        return binding.root
    }
}
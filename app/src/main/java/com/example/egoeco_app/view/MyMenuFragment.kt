package com.example.egoeco_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.FragmentMyMenuBinding
import com.example.egoeco_app.databinding.FragmentPublicDataBinding
import com.example.egoeco_app.viewmodel.MyMenuViewModel
import com.trello.rxlifecycle4.components.support.RxFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMenuFragment : RxFragment() {
    private val binding: FragmentMyMenuBinding by lazy {
        FragmentMyMenuBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: MyMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_menu, container, false)
    }
}
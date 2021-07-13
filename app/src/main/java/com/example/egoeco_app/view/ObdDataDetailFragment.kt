package com.example.egoeco_app.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.example.egoeco_app.databinding.FragmentObdDataDetailBinding
import com.example.egoeco_app.viewmodel.ObdDataDetailViewModel
import com.trello.rxlifecycle4.components.support.RxDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException

@AndroidEntryPoint
class ObdDataDetailFragment(val id: Long) : RxDialogFragment() {
    private val binding by lazy { FragmentObdDataDetailBinding.inflate(layoutInflater) }
    private val viewModel: ObdDataDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding.apply {
            viewModel = this@ObdDataDetailFragment.viewModel.apply { getOBDDataById(id) }
            lifecycleOwner = this@ObdDataDetailFragment
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setView(binding.root)
            }
            builder.create()
        } ?: throw IllegalStateException("Fragment cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}
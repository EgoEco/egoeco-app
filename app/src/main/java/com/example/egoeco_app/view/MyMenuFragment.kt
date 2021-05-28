package com.example.egoeco_app.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.egoeco_app.databinding.FragmentMyMenuBinding
import com.example.egoeco_app.viewmodel.MainViewModel
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
            vehicleInfoButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://egoeco.netlify.app/analysis"))
                startActivity(browserIntent)
            }
            pointShoppingMallButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://egoeco.netlify.app/shop"))
                startActivity(browserIntent)
            }
            publicDataButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://egoeco.netlify.app/news"))
                startActivity(browserIntent)
            }
            pointCheckButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://egoeco.netlify.app/login"))
                startActivity(browserIntent)
            }
        }
        return binding.root
    }
}
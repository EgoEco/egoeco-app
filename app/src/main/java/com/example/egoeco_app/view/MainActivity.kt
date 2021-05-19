package com.example.egoeco_app.view

import android.os.Bundle
import androidx.activity.viewModels
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.ActivityMainBinding
import com.example.egoeco_app.viewmodel.MainViewModel
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

class MainActivity : RxAppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
        }
    }
}
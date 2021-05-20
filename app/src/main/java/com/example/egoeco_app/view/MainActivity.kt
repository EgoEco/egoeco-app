package com.example.egoeco_app.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.ActivityMainBinding
import com.example.egoeco_app.viewmodel.MainViewModel
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import splitties.toast.toast

@AndroidEntryPoint
class MainActivity : RxAppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.apply {
            setupWithNavController(navController)
            selectedItemId = R.id.obdDataFragment
        }
    }
}
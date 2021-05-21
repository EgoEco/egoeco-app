package com.example.egoeco_app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.egoeco_app.R
import com.example.egoeco_app.databinding.ActivityLoginBinding
import com.example.egoeco_app.viewmodel.LoginViewModel
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : RxAppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Egoecoapp)
        setContentView(binding.root)
        binding.apply {
            viewModel = this@LoginActivity.viewModel
            lifecycleOwner = this@LoginActivity
            loginButton.setOnClickListener {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("loginId", idTextEdit.text)
                intent.putExtra("loginPw", pwTextEdit.text)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
    }
}
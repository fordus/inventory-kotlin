package com.example.inventory.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventory.R
import com.example.inventory.databinding.ActivityDataBinding
import com.example.inventory.viewmodel.UserViewModel

class DataActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: ActivityDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.Black.toArgb()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
        setupClickListeners()
    }

    private fun initUI() {
        binding.stats.text = userViewModel.getStringStats()
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

}
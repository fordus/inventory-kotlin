package com.example.inventory.view

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.inventory.R
import com.example.inventory.databinding.ActivityLoginBinding
import com.example.inventory.databinding.CustomToastBinding
import com.example.inventory.model.ModelDataUser
import com.example.inventory.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.Black.toArgb()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }


    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {

            val username = binding.inputUsername.text.toString()
            val password = binding.inputPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showCustomToast("Please fill in all fields")
                return@setOnClickListener
            }

            val user = userViewModel.checkIfUserExists(username, password)

            if (user != null) {
                userViewModel.loginUser(username, password)
                showCustomToast("Welcome back, ${user.username}")
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            } else {

                println("user: $user")
                println("allUsers: ${userViewModel.getAllUsers()}")

                showCustomToast("Invalid username or password")
            }
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showCustomToast(message: String) {
        val binding = CustomToastBinding.inflate(layoutInflater)
        binding.toastText.text = message
        val toast = Toast(applicationContext).apply {
            duration = Toast.LENGTH_SHORT
            view = binding.root
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        }
        toast.show()
    }
}

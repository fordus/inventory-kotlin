package com.example.inventory.view

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import com.example.inventory.databinding.ActivityRegisterBinding
import com.example.inventory.databinding.CustomToastBinding
import com.example.inventory.viewmodel.UserViewModel
import androidx.appcompat.app.AppCompatActivity
import com.example.inventory.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.BLACK

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            finish()
        }

        binding.buttonRegister.setOnClickListener {
            val username = binding.inputUsername.text.toString()
            val password = binding.inputPassword.text.toString()
            val confirmPassword = binding.inputConfirmPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showCustomToast("Please fill in all fields")
            } else if (password != confirmPassword) {
                showCustomToast("Passwords do not match")
            } else {
                val userExists = userViewModel.checkIfUsernameExists(username)
                if (userExists) {
                    showCustomToast("Username already exists")
                    return@setOnClickListener
                }

                val newUser = userViewModel.createUser(username, password)
                userViewModel.addUser(newUser)

                showCustomToast("Account created successfully")

                println("New user: $newUser")
                println("All users: ${userViewModel.getAllUsers()}")
                finish()
            }
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
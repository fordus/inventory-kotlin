package com.example.inventory.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.inventory.R
import com.example.inventory.adapter.ItemAdapter
import com.example.inventory.databinding.ActivityDashboardBinding
import com.example.inventory.databinding.CustomToastBinding
import com.example.inventory.model.ModelItem
import com.example.inventory.viewmodel.UserViewModel
import java.util.UUID

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.Black.toArgb()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
        setupClickListeners()
        observerViewModel()
    }

    private fun initUI() {
        val currentUser = userViewModel.currentUser.value
        val username = currentUser?.username ?: "User"
        binding.tvWelcomeMessage.text = getString(R.string.welcome_message, username)

        val currentUserItems = userViewModel.getCurrentUserItems().toMutableList()

        itemAdapter = ItemAdapter(currentUserItems) { item ->
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("itemId", item.id)
            startActivity(intent)
        }
        binding.recyclerViewItems.layoutManager = GridLayoutManager(this, 1)
        binding.recyclerViewItems.adapter = itemAdapter
    }

    private fun setupClickListeners() {
        binding.buttonLogout.setOnClickListener {
            userViewModel.logoutUser()
            showCustomToast("Logged out")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.buttonStatistics.setOnClickListener {
            startActivity(Intent(this, DataActivity::class.java))
        }

        binding.buttonAddItem.setOnClickListener {
            showDialog()
        }

        //TextWatcher a la barra de búsqueda (searchBar) para escuchar los cambios de texto
        binding.searchBar.addTextChangedListener(object : TextWatcher {

            // Método que se ejecuta justo antes de que el texto en el EditText cambie
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            // Método que se ejecuta mientras el texto cambia
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Se llama al filtro del adaptador y se le pasa el texto actual del EditText (convertido a String)
                // Esto actualizará el RecyclerView con los elementos que coincidan con el texto introducido
                itemAdapter.filter.filter(s.toString())
            }

            // Método que se ejecuta después de que el texto ha cambiado
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_item_dialog)

        val buttonBack = dialog.findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener { dialog.dismiss() }

        val buttonCreateProduct = dialog.findViewById<Button>(R.id.buttonCreateProduct)
        buttonCreateProduct.setOnClickListener {
            val name = dialog.findViewById<EditText>(R.id.inputProductName).text.toString()
            val description =
                dialog.findViewById<EditText>(R.id.inputProductDescription).text.toString()
            val price = dialog.findViewById<EditText>(R.id.inputProductPrice).text.toString()
            val quantity = dialog.findViewById<EditText>(R.id.inputProductQuantity).text.toString()
            val category = dialog.findViewById<EditText>(R.id.inputProductCategory).text.toString()
            val image = dialog.findViewById<EditText>(R.id.productImage).text.toString()

            if (name.isEmpty() || description.isEmpty() || price.isEmpty() || quantity.isEmpty() || category.isEmpty() || image.isEmpty()) {
                showCustomToast("Please fill in all fields")
                return@setOnClickListener
            }

            try {
                val priceValue = price.toInt()
                val quantityValue = quantity.toInt()

                val item = ModelItem(
                    UUID.randomUUID().toString(),
                    name,
                    description,
                    image,
                    priceValue,
                    quantityValue,
                    category
                )
                userViewModel.addItem(item)
                showCustomToast("Item added")
                dialog.dismiss()
            } catch (e: NumberFormatException) {
                showCustomToast("Price and quantity must be numbers")
            }
        }

        dialog.show()
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

    private fun observerViewModel() {
        userViewModel.currentUser.observe(
            this,
            Observer {
                itemAdapter.updateItems(userViewModel.getCurrentUserItems())
                println("Items updated")
            }
        )
    }
}

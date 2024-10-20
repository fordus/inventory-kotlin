package com.example.inventory.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.window.Dialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.inventory.R
import com.example.inventory.adapter.ItemAdapter
import com.example.inventory.databinding.ActivityDetailsBinding
import com.example.inventory.databinding.CustomToastBinding
import com.example.inventory.model.ModelItem
import com.example.inventory.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import java.util.UUID

class DetailsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDetailsBinding
    private val userViewModel: UserViewModel by viewModels()
    private var imageURL = ""
    private var currentItemId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
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
        val itemId = intent.getStringExtra("itemId") ?: ""
        val item = userViewModel.getItemById(itemId)

        Picasso.get().load(item?.image).into(binding.itemImage)

        imageURL = item?.image.toString()
        currentItemId = item?.id.toString()

        binding.tvItemTitle.text = item?.name
        binding.tvItemDescription.text = item?.description
        binding.tvItemPrice.text = item?.price.toString()
        binding.tvItemQuantity.text = item?.quantity.toString()
        binding.itemCategory.text = item?.category
    }


    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            navigateToDashboard()
            finish()
        }

        binding.buttonDelete.setOnClickListener {
            val itemId = intent.getStringExtra("itemId") ?: ""

            val item = userViewModel.getItemById(itemId)

            if (item != null) {
                userViewModel.deleteItem(item)
                showCustomToast("Item deleted")
                navigateToDashboard()
                finish()
            } else {
                showCustomToast("Item not found")
            }

        }

        binding.buttonEdit.setOnClickListener {
            showDialog()
        }

    }


    private fun showDialog() {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.edit_item_dialog)

        val buttonBack = dialog.findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener { dialog.dismiss() }

        val itemImage = dialog.findViewById<EditText>(R.id.productImage)
        val itemName = dialog.findViewById<EditText>(R.id.inputProductName)
        val itemDescription = dialog.findViewById<EditText>(R.id.inputProductDescription)
        val itemPrice = dialog.findViewById<EditText>(R.id.inputProductPrice)
        val itemQuantity = dialog.findViewById<EditText>(R.id.inputProductQuantity)
        val itemCategory = dialog.findViewById<EditText>(R.id.inputProductCategory)

        itemImage.setText(imageURL)
        itemName.setText(binding.tvItemTitle.text)
        itemDescription.setText(binding.tvItemDescription.text)
        itemPrice.setText(binding.tvItemPrice.text)
        itemQuantity.setText(binding.tvItemQuantity.text)
        itemCategory.setText(binding.itemCategory.text)

        val buttonEditProduct = dialog.findViewById<Button>(R.id.buttonEditProduct)

        buttonEditProduct.setOnClickListener(){
            val name = itemName.text.toString()
            val description = itemDescription.text.toString()
            val price = itemPrice.text.toString()
            val quantity = itemQuantity.text.toString()
            val category = itemCategory.text.toString()
            val image = itemImage.text.toString()

            if (name.isEmpty() || description.isEmpty() || price.isEmpty() || quantity.isEmpty() || category.isEmpty() || image.isEmpty()) {
                showCustomToast("Please fill in all fields")
                return@setOnClickListener
            }

            try {
                val priceValue = price.toInt()
                val quantityValue = quantity.toInt()

                val item = ModelItem(
                    currentItemId,
                    name,
                    description,
                    image,
                    priceValue,
                    quantityValue,
                    category
                )
                userViewModel.updateItem(item)
                showCustomToast("Item updated")
                println(userViewModel.getItemById(item.id))

                binding.tvItemTitle.text = name
                binding.tvItemDescription.text = description
                binding.tvItemPrice.text = price
                binding.tvItemQuantity.text = quantity
                binding.itemCategory.text = category
                Picasso.get().load(image).into(binding.itemImage)

                dialog.dismiss()
            } catch (e: NumberFormatException) {
                showCustomToast("Price and quantity must be numbers")
            }
        }

        dialog.show()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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

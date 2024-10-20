package com.example.inventory.model

data class ModelItem(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val price: Int,
    val quantity: Int,
    val category: String
) {
}
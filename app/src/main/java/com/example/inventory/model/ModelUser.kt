package com.example.inventory.model

data class ModelUser(
    val id: String,
    val username: String,
    val password: String,
    val items: MutableList<ModelItem>
) {
}

object ModelDataUser {
    val users: MutableList<ModelUser> = mutableListOf()
}
package com.example.inventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inventory.model.ModelCurrentUser
import com.example.inventory.model.ModelUser
import com.example.inventory.model.ModelDataUser
import com.example.inventory.model.ModelItem
import java.util.UUID

class UserViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData que contiene la lista de usuarios y se expone para que la UI observe los cambios
    private val _dataListUser: MutableLiveData<MutableList<ModelUser>> = MutableLiveData(
        ModelDataUser.users // Inicializa con la lista de usuarios almacenada en ModelDataUser
    )

    // LiveData pública que expone la lista de usuarios a la UI
    val dataListUser: LiveData<MutableList<ModelUser>>
        get() = _dataListUser

    // LiveData que contiene el usuario actual autenticado
    private val _currentUser: MutableLiveData<ModelUser?> = MutableLiveData(ModelCurrentUser.currentUser)

    // LiveData pública que expone el usuario actual
    val currentUser: LiveData<ModelUser?>
        get() = _currentUser

    // Función para agregar un nuevo usuario a la lista de usuarios
    fun addUser(user: ModelUser) {
        ModelDataUser.users.add(user)
        _dataListUser.value = ModelDataUser.users // Notifica a los observadores que la lista ha cambiado
    }

    // Verifica si un nombre de usuario ya existe en la lista de usuarios
    fun checkIfUsernameExists(username: String): Boolean {
        return ModelDataUser.users.any { it.username == username }
    }

    // Crea un nuevo usuario con los datos provistos, pero no lo agrega a la lista de usuarios
    fun createUser(username: String, password: String): ModelUser {
        val exampleItems = mutableListOf(
            ModelItem(createId(), "Item 1", "Description 1", "https://picsum.photos/402", 100, 10, "Category 1"),
            ModelItem(createId(), "Item 2", "Description 2", "https://picsum.photos/403", 200, 20, "Category 2"),
            ModelItem(createId(), "Item 3", "Description 3", "https://picsum.photos/404", 300, 30, "Category 3"),
            ModelItem(createId(), "Item 4", "Description 4", "https://picsum.photos/405", 400, 40, "Category 4"),
            ModelItem(createId(), "Item 5", "Description 5", "https://picsum.photos/407", 500, 50, "Category 5")
        )

        // Retorna un nuevo ModelUser con los datos y los items de ejemplo
        return ModelUser(createId(), username, password, exampleItems)
    }

    // Genera un ID único usando UUID
    private fun createId(): String {
        return UUID.randomUUID().toString()
    }

    // Verifica si existe un usuario con el nombre de usuario y contraseña
    fun checkIfUserExists(username: String, password: String): ModelUser? {
        return ModelDataUser.users.find { it.username == username && it.password == password }
    }

    // Inicia sesión estableciendo el usuario actual si las credenciales son correctas
    fun loginUser(username: String, password: String): Boolean {
        val user = checkIfUserExists(username, password)
        return if (user != null) {
            ModelCurrentUser.currentUser = user
            _currentUser.value = user
            true
        } else {
            false
        }
    }

    // Cierra la sesión del usuario actual
    fun logoutUser() {
        ModelCurrentUser.currentUser = null // Limpia el usuario actual globalmente
        _currentUser.value = null // Notifica a los observadores que no hay usuario autenticado
    }


    // Obtiene todos los usuarios actualmente almacenados
    fun getAllUsers(): List<ModelUser> {
        return ModelDataUser.users
    }

    // Agrega un item al usuario actual
    fun addItem(item: ModelItem) {
        val user = ModelCurrentUser.currentUser
        user?.items?.add(item) // Agrega el item a la lista de items del usuario actual
        _currentUser.value = user // Actualiza el LiveData del usuario actual
    }

    // Elimina un item del usuario actual
    fun deleteItem(item: ModelItem) {
        val user = ModelCurrentUser.currentUser
        user?.items?.remove(item)
        _currentUser.value = user // Actualiza el LiveData del usuario actual
    }

    // Obtiene la lista de items del usuario actual
    fun getCurrentUserItems(): MutableList<ModelItem> {
        return ModelCurrentUser.currentUser?.items?.toMutableList() ?: mutableListOf()
    }


    // Busca un item por su ID en la lista de items del usuario actual
    fun getItemById(itemId: String): ModelItem? {
        return ModelCurrentUser.currentUser?.items?.find { it.id == itemId }
    }

    // Actualiza un item existente del usuario actual
    fun updateItem(item: ModelItem) {
        val user = ModelCurrentUser.currentUser?.items?.toMutableList()?.apply {
            // Busca el índice del item que se quiere actualizar
            val index = indexOfFirst { it.id == item.id }
            if (index != -1) {
                set(index, item) // Actualiza el item en la lista
            }
        }?.let { ModelCurrentUser.currentUser?.copy(items = it) } // Crea una copia del usuario actual con los items actualizados
        _currentUser.value = user // Notifica a los observadores sobre el cambio
        ModelCurrentUser.currentUser = user // Actualiza el usuario actual globalmente
    }

    // Genera un resumen estadístico
    fun getStringStats(): String {
        val items = ModelCurrentUser.currentUser?.items ?: emptyList()
        val totalItems = items.size
        val totalValue = items.sumBy { it.price * it.quantity }
        val averagePrice = items.map { it.price }.average()
        val averageQuantity = items.map { it.quantity }.average()
        val mostExpensiveItem = items.maxByOrNull { it.price * it.quantity }
        val leastExpensiveItem = items.minByOrNull { it.price * it.quantity }
        val mostExpensiveItemValue = mostExpensiveItem?.price?.times(mostExpensiveItem.quantity)
        val leastExpensiveItemValue = leastExpensiveItem?.price?.times(leastExpensiveItem.quantity)
        val mostExpensiveItemName = mostExpensiveItem?.name
        val leastExpensiveItemName = leastExpensiveItem?.name

        return """
            Total items: $totalItems
            Total value: $totalValue
            Average price: $averagePrice
            Average quantity: $averageQuantity
            Most expensive item: $mostExpensiveItemName with a value of $mostExpensiveItemValue
            Least expensive item: $leastExpensiveItemName with a value of $leastExpensiveItemValue
        """.trimIndent()
    }
}

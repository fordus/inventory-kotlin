package com.example.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.R
import com.example.inventory.model.ModelItem
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.*

class ItemAdapter(
    private val originalItems: List<ModelItem>, // Lista original de items (no filtrada)
    private val onItemSelected: (item: ModelItem) -> Unit // Función callback que se ejecuta cuando se selecciona un item
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(), Filterable {

    // Lista mutable que contiene los items filtrados (inicia con todos los items)
    private var items: MutableList<ModelItem> = originalItems.toMutableList()

    // Infla el layout de cada item (item_inventory) y crea el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return ItemViewHolder(view)
    }

    // Enlaza los datos del item con la vista en el ViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.initialize(items[position], onItemSelected) // Se pasa el item actual y la función callback
    }

    // Devuelve el número de items actualmente filtrados
    override fun getItemCount() = items.size

    // Método para actualizar la lista de items
    fun updateItems(newItems: List<ModelItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Implementación del método getFilter, que devuelve el filtro para buscar items
    override fun getFilter(): Filter {
        return object : Filter() {

            // Método para realizar el filtrado en segundo plano
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.ROOT) ?: "" // Convierte la consulta a minúsculas

                // Si no hay texto en el filtro, se muestra la lista original; de lo contrario, se filtra
                val filteredList = if (query.isEmpty()) {
                    originalItems
                } else {
                    originalItems.filter {
                        // Filtra los items cuyo nombre o categoría contengan la consulta
                        it.name.lowercase(Locale.ROOT).contains(query) ||
                                it.category.lowercase(Locale.ROOT).contains(query)
                    }
                }

                // Se crea un FilterResults para devolver los resultados filtrados
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            // Publica los resultados del filtro (actualiza la lista de items mostrados)
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Actualiza la lista de items visibles con los resultados filtrados
                items = (results?.values as? List<ModelItem>)?.toMutableList() ?: mutableListOf()
                notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
            }
        }
    }

    // Clase ViewHolder que contiene las referencias a las vistas del layout
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        private val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
        private val itemImage: ImageView = itemView.findViewById(R.id.itemThumbnail)
        private val itemCategory: TextView = itemView.findViewById(R.id.itemCategory)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        // Inicializa las vistas con los datos del item y configura el listener para seleccionar el item
        fun initialize(item: ModelItem, action: (ModelItem) -> Unit) {
            itemName.text = String.format("%s", item.name)
            itemPrice.text = String.format("Price: %s", item.price)
            itemQuantity.text = String.format("Quantity: %s", item.quantity)
            itemCategory.text = String.format("%s", item.category)
            loadImage(item.image) // Carga la imagen del item con Picasso

            itemView.setOnClickListener {
                action(item)
            }
        }

        // Carga la imagen del item usando Picasso
        private fun loadImage(imageUrl: String?) {
            if (!imageUrl.isNullOrEmpty()) {
                itemImage.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                Picasso.get()
                    .load(imageUrl)
                    .into(itemImage, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                            itemImage.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                            itemImage.visibility = View.VISIBLE
                            itemImage.setImageResource(R.drawable.ic_inventory_placeholder)
                        }
                    })
            } else {
                itemImage.setImageResource(R.drawable.ic_inventory_placeholder)
                progressBar.visibility = View.GONE
            }
        }
    }
}

package flores.pablo.sazonforaneo.ui.categorias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import flores.pablo.sazonforaneo.ui.Categoria
import flores.pablo.sazonforaneo.R

class CategoriaAdapter(
    private var categorias: List<Categoria>,  // Cambiado a var para actualizar
    private val clickListener: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.category_image)
        val nameView: TextView = itemView.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.nameView.text = categoria.nombre
        holder.imageView.setImageResource(categoria.idImagenRes)

        holder.itemView.setOnClickListener { clickListener(categoria) }
    }

    override fun getItemCount(): Int = categorias.size

    // Método para actualizar la lista y refrescar el RecyclerView
    fun actualizarLista(nuevaLista: List<Categoria>) {
        categorias = nuevaLista
        notifyDataSetChanged()
    }
}

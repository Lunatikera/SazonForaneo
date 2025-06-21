package flores.pablo.sazonforaneo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoriaAdapter(
    private var lista: List<Categoria>,
    private val onClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgCategoria)
        val txt = view.findViewById<TextView>(R.id.txtCategoria)

        fun bind(categoria: Categoria) {
            img.setImageResource(categoria.imagenRes)
            txt.text = categoria.nombre
            itemView.setOnClickListener { onClick(categoria) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    fun actualizarLista(nueva: List<Categoria>) {
        lista = nueva
        notifyDataSetChanged()
    }
}

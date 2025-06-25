package flores.pablo.sazonforaneo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EtiquetaAdapter(
private val etiquetas: MutableList<String>,
private val onEliminar: (String) -> Unit
) : RecyclerView.Adapter<EtiquetaAdapter.EtiquetaViewHolder>() {

    inner class EtiquetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEtiqueta: TextView = itemView.findViewById(R.id.tvEtiqueta)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtiquetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_etiqueta, parent, false)
        return EtiquetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EtiquetaViewHolder, position: Int) {
        val etiqueta = etiquetas[position]
        holder.tvEtiqueta.text = etiqueta
        holder.btnEliminar.setOnClickListener {
            val etiquetaEliminada = etiquetas[holder.adapterPosition]
            etiquetas.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            onEliminar(etiquetaEliminada)
        }
    }

    override fun getItemCount(): Int = etiquetas.size
}

package flores.pablo.sazonforaneo.ui.explorar

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta

class ExplorarAdapter(
    recetasIniciales: List<Receta>,
    private val onItemClick: (Receta) -> Unit
) : RecyclerView.Adapter<ExplorarAdapter.RecetaViewHolder>() {

    private var recetas = recetasIniciales.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        holder.bind(recetas[position])
    }

    override fun getItemCount(): Int = recetas.size

    fun actualizarLista(nuevaLista: List<Receta>) {
        recetas.clear()
        recetas.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivReceta: ImageView = itemView.findViewById(R.id.ivReceta)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreReceta)
        private val tvAutor: TextView = itemView.findViewById(R.id.tvAutor)
        private val tagsLayout: LinearLayout = itemView.findViewById(R.id.tagsLayout)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)

        fun bind(receta: Receta) {
            tvNombre.text = receta.nombre
            tvAutor.text = "Autor: ${receta.autor}"

            cargarImagenReceta(receta.imagenUriString)

            ratingBar.rating = receta.rating.coerceIn(0f, 5f)
            tvRating.text = String.format("%.1f", receta.rating)

            tagsLayout.removeAllViews()
            receta.etiquetas.forEach { tag ->
                val tagView = TextView(itemView.context).apply {
                    text = tag
                    setPadding(16, 4, 16, 4)
                    textSize = 12f
                    setTextColor(android.graphics.Color.WHITE)
                    setBackgroundResource(R.drawable.tag_green_background)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(8, 0, 0, 0) }
                }
                tagsLayout.addView(tagView)
            }

            itemView.setOnClickListener { onItemClick(receta) }
        }

        private fun cargarImagenReceta(uriString: String?) {
            val placeholder = R.drawable.pizza
            val context = itemView.context

            if (uriString.isNullOrEmpty()) {
                ivReceta.setImageResource(placeholder)
                return
            }

            if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                Glide.with(context).load(uriString).placeholder(placeholder).into(ivReceta)
                return
            }

            val uri = Uri.parse(uriString)

            try {
                // Glide con content/file uri
                Glide.with(context).load(uri).placeholder(placeholder).into(ivReceta)
            } catch (e: Exception) {
                // Fallback: abrir stream por ContentResolver y decodificar Bitmap
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bmp = BitmapFactory.decodeStream(stream)
                        Glide.with(context).load(bmp).placeholder(placeholder).into(ivReceta)
                    } ?: ivReceta.setImageResource(placeholder)
                } catch (e2: Exception) {
                    ivReceta.setImageResource(placeholder)
                }
            }
        }
    }
}

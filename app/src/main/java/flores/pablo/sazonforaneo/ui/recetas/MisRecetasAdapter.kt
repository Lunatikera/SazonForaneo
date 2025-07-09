package flores.pablo.sazonforaneo.ui.recetas

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.UsuarioRepository

class MisRecetasAdapter(
    private val recetas: List<Receta>,
    private val usuarioRepo: UsuarioRepository,
    private val onItemClick: (Receta) -> Unit
) : RecyclerView.Adapter<MisRecetasAdapter.RecetaViewHolder>() {

    // Cache local para nombres ya consultados
    private val nombreCache = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        holder.bind(recetas[position])
    }

    override fun getItemCount(): Int = recetas.size

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivReceta: ImageView = itemView.findViewById(R.id.ivReceta)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreReceta)
        private val tvAutor: TextView = itemView.findViewById(R.id.tvAutor)
        private val tagsLayout: LinearLayout = itemView.findViewById(R.id.tagsLayout)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)

        fun bind(receta: Receta) {
            tvNombre.text = receta.nombre

            val autorId = receta.autorId
            if (autorId.isNotEmpty()) {
                val cachedNombre = nombreCache[autorId]
                if (cachedNombre != null) {
                    tvAutor.text = "Autor: $cachedNombre"
                } else {
                    tvAutor.text = "Autor: cargando..."
                    usuarioRepo.obtenerNombrePorId(autorId,
                        onSuccess = { nombreActualizado ->
                            nombreCache[autorId] = nombreActualizado
                            tvAutor.text = "Autor: $nombreActualizado"
                        },
                        onError = {
                            tvAutor.text = "Autor: Desconocido"
                        }
                    )
                }
            } else {
                tvAutor.text = "Autor: Desconocido"
            }

            // Cargar imagen
            val imagenUrl = receta.imagenUriString
            if (!imagenUrl.isNullOrEmpty()) {
                if (imagenUrl.startsWith("http://") || imagenUrl.startsWith("https://")) {
                    Glide.with(itemView.context)
                        .load(imagenUrl)
                        .placeholder(R.drawable.pizza)
                        .into(ivReceta)
                } else {
                    val uri = Uri.parse(imagenUrl)
                    Glide.with(itemView.context)
                        .load(uri)
                        .placeholder(R.drawable.pizza)
                        .into(ivReceta)
                }
            } else {
                ivReceta.setImageResource(R.drawable.pizza)
            }

            ratingBar.rating = receta.rating.coerceIn(0f, 5f)
            tvRating.text = String.format("%.1f", receta.rating)

            tagsLayout.removeAllViews()
            val allTags = receta.categorias + receta.etiquetas
            for (tag in allTags) {
                val tagView = TextView(itemView.context).apply {
                    text = tag
                    setPadding(16, 4, 16, 4)
                    textSize = 12f
                    setTextColor(android.graphics.Color.WHITE)
                    setBackgroundResource(R.drawable.tag_green_background)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(8, 0, 0, 0)
                    layoutParams = params
                }
                tagsLayout.addView(tagView)
            }

            itemView.setOnClickListener {
                onItemClick(receta)
            }
        }
    }
}

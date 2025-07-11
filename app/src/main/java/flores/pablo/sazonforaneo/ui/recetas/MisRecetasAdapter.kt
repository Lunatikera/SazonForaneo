package flores.pablo.sazonforaneo.ui.recetas

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import flores.pablo.sazonforaneo.AgregarNombreDescripcion
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.UsuarioRepository

class MisRecetasAdapter(

    var where_from: Boolean,
    var recetas: List<Receta>,
    private val usuarioRepo: UsuarioRepository,
    private val onItemClick: (Receta) -> Unit,
    private val onEliminarClick: (Receta) -> Unit,
    private val onEditarClick: (Receta) -> Unit,
    private val onCambiarVisibilidadClick: (Receta) -> Unit

) : RecyclerView.Adapter<MisRecetasAdapter.RecetaViewHolder>() {

    private val nombreCache = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_misreceta, parent, false)
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

        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        private val btnVisibility: ImageButton = itemView.findViewById(R.id.btnVisibilidad)

        fun bind(receta: Receta) {
            if (!where_from) {
                btnEliminar.visibility = View.GONE
                btnEditar.visibility = View.GONE
                btnVisibility.visibility = View.GONE
            } else {
                btnEliminar.visibility = View.VISIBLE
                btnEditar.visibility = View.VISIBLE
                btnVisibility.visibility = View.VISIBLE
            }

            tvNombre.text = receta.nombre

            val autorId = receta.autorId
            if (autorId.isNotEmpty()) {
                val cachedNombre = nombreCache[autorId]
                if (cachedNombre != null) {
                    tvAutor.text = "Autor: $cachedNombre"
                } else {
                    usuarioRepo.obtenerNombrePorId(
                        autorId,
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
                val uri = Uri.parse(imagenUrl)
                Glide.with(itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.pizza)
                    .into(ivReceta)
            } else {
                ivReceta.setImageResource(R.drawable.pizza)
            }

            ratingBar.rating = receta.rating.coerceIn(0f, 5f)
            tvRating.text = String.format("%.1f", receta.rating)

            // Etiquetas
            tagsLayout.removeAllViews()
            val allTags = receta.etiquetas
            for (tag in allTags) {
                val tagView = TextView(itemView.context).apply {
                    text = tag
                    setPadding(16, 4, 16, 4)
                    textSize = 12f
                    setTextColor(android.graphics.Color.WHITE)
                    setBackgroundResource(R.drawable.tag_green_background)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 0, 0, 0)
                    }
                }
                tagsLayout.addView(tagView)
            }

            itemView.setOnClickListener {
                onItemClick(receta)
            }

            btnEliminar.setOnClickListener {
                onEliminarClick(receta)
            }

            btnEditar.setOnClickListener {
                //aqui empieza el show para editar
                val intent = Intent(itemView.context, AgregarNombreDescripcion::class.java)
                intent.putExtra("receta_para_editar", receta) // se pasa la receta completa
                itemView.context.startActivity(intent)
            }

            // icono de visibilidad basado en String
            actualizarIconoVisibilidad(receta.visibilidad)

            btnVisibility.setOnClickListener {
                // aqui NO cambiamos visibilidad localmente, solo avisamos al fragmento
                onCambiarVisibilidadClick(receta)
            }
        }

        private fun actualizarIconoVisibilidad(visibilidad: String) {
            val iconRes = if (visibilidad == "publico") {
                R.drawable.ic_visible
            } else {
                R.drawable.ic_visible_off
            }
            btnVisibility.setImageResource(iconRes)
        }
    }
}

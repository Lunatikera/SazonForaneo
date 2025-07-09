package flores.pablo.sazonforaneo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout

class DetalleReceta : AppCompatActivity() {

    private lateinit var ivReceta: ImageView
    private lateinit var tvTitulo: TextView
    private lateinit var tvAutor: TextView
    private lateinit var ivPerfil: ImageView
    private lateinit var ivFav: ImageView
    private lateinit var layoutTags: FlexboxLayout
    private lateinit var tvCalificacion: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvFuente: TextView
    private lateinit var tabIngredientes: TextView
    private lateinit var tabInstrucciones: TextView
    private lateinit var tvContenidoReceta: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnCalificar: Button

    private lateinit var receta: Receta
    private var isFavorite = false

    private val usuarioRepo = UsuarioRepository()
    private val calificacionRepo = CalificacionRepository()
    private val favoritosRepo = FavoritosRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_receta)

        ivReceta = findViewById(R.id.ivReceta)
        tvTitulo = findViewById(R.id.tvTitulo)
        tvAutor = findViewById(R.id.tvAutor)
        ivPerfil = findViewById(R.id.ivPerfil)
        ivFav = findViewById(R.id.ivFav)
        layoutTags = findViewById(R.id.layoutTags)
        tvCalificacion = findViewById(R.id.tvCalificacion)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        tvFuente = findViewById(R.id.tvFuente)
        tabIngredientes = findViewById(R.id.tabIngredientes)
        tabInstrucciones = findViewById(R.id.tabInstrucciones)
        tvContenidoReceta = findViewById(R.id.tvContenidoReceta)
        ratingBar = findViewById(R.id.ratingBar)
        btnCalificar = findViewById(R.id.btnCalificar)

        receta = intent.getSerializableExtra("receta") as Receta

        mostrarDatos(receta)
        configurarTabs()
        configurarBotonCalificar()

        cargarCalificacionPromedio()
        cargarEstadoFavorito()
        configurarFavorito()
    }

    private fun mostrarDatos(receta: Receta) {
        tvTitulo.text = receta.nombre

        Glide.with(this)
            .load(receta.imagenUriString)
            .placeholder(R.drawable.pizza)
            .into(ivReceta)

        if (receta.autorId.isNullOrEmpty()) {
            tvAutor.text = "Anonimo"
            ivPerfil.setImageResource(R.drawable.imagen_predeterminada)
        } else {
            usuarioRepo.obtenerDatosUsuarioPorId(
                receta.autorId,
                onSuccess = { nombreAutor, fotoPerfilUrl ->
                    tvAutor.text = nombreAutor
                    if (!fotoPerfilUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(fotoPerfilUrl)
                            .circleCrop()
                            .into(ivPerfil)
                    } else {
                        ivPerfil.setImageResource(R.drawable.imagen_predeterminada)
                    }
                },
                onError = {
                    tvAutor.text = "Anonimo"
                    ivPerfil.setImageResource(R.drawable.imagen_predeterminada)
                }
            )
        }

        tvCalificacion.text = String.format("%.1f", receta.rating)
        tvDescripcion.text = receta.descripcion

        tvFuente.text = "Fuente de la Receta"
        tvFuente.setOnClickListener {
            val url = receta.fuente
            if (!url.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else {
                Toast.makeText(this, "No hay una fuente disponible", Toast.LENGTH_SHORT).show()
            }
        }

        ratingBar.rating = receta.rating

        layoutTags.removeAllViews()
        for (tag in receta.etiquetas) {
            val tagView = TextView(this).apply {
                text = tag
                setPadding(24, 12, 24, 12)
                setTextColor(android.graphics.Color.WHITE)
                setBackgroundResource(R.drawable.tag_green_background)
                layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 16, 16) }
            }
            layoutTags.addView(tagView)
        }

        tvContenidoReceta.text = receta.ingredientes.joinToString("\n") { "• $it" }
    }

    private fun configurarTabs() {
        tabIngredientes.setOnClickListener {
            tabIngredientes.setBackgroundResource(R.drawable.tab_unselected)
            tabIngredientes.setTextColor(resources.getColor(R.color.black))

            tabInstrucciones.setBackgroundResource(R.drawable.tab_selected)
            tabInstrucciones.setTextColor(resources.getColor(R.color.medium_gray))

            tvContenidoReceta.text = receta.ingredientes.joinToString("\n") { "• $it" }
        }

        tabInstrucciones.setOnClickListener {
            tabInstrucciones.setBackgroundResource(R.drawable.tab_unselected)
            tabInstrucciones.setTextColor(resources.getColor(R.color.black))

            tabIngredientes.setBackgroundResource(R.drawable.tab_selected)
            tabIngredientes.setTextColor(resources.getColor(R.color.medium_gray))

            tvContenidoReceta.text = receta.instrucciones
        }
    }

    private fun cargarCalificacionPromedio() {
        if (receta.id == null) return

        calificacionRepo.obtenerPromedioCalificacion(receta.id!!, { promedio ->
            ratingBar.rating = promedio
            tvCalificacion.text = String.format("%.1f", promedio)
        }, { errorMsg ->
            Toast.makeText(this, "Error al cargar calificación: $errorMsg", Toast.LENGTH_SHORT).show()
        })
    }

    private fun cargarEstadoFavorito() {
        if (receta.id == null) return

        favoritosRepo.esFavorito(receta.id!!, { esFav ->
            isFavorite = esFav
            actualizarIconoFavorito()
        }, { errorMsg ->
        })
    }

    private fun actualizarIconoFavorito() {
        val icono = if (isFavorite) R.drawable.favheart_filled else R.drawable.favheart
        ivFav.setImageResource(icono)
    }

    private fun configurarFavorito() {
        ivFav.setOnClickListener {
            if (receta.id == null) return@setOnClickListener

            if (isFavorite) {
                favoritosRepo.eliminarFavorito(receta.id!!, {
                    isFavorite = false
                    actualizarIconoFavorito()
                    Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }, { errorMsg ->
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                })
            } else {
                favoritosRepo.agregarFavorito(receta.id!!, {
                    isFavorite = true
                    actualizarIconoFavorito()
                    Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                }, { errorMsg ->
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    private fun configurarBotonCalificar() {
        btnCalificar.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_rating, null)
            val ratingBarDialog = dialogView.findViewById<RatingBar>(R.id.ratingBarDialog)

            AlertDialog.Builder(this)
                .setTitle("Calificar receta")
                .setView(dialogView)
                .setPositiveButton("Enviar") { _, _ ->
                    val rating = ratingBarDialog.rating
                    if (receta.id == null) {
                        Toast.makeText(this, "Receta inválida", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    calificacionRepo.guardarCalificacion(receta.id!!, rating, {
                        cargarCalificacionPromedio()
                        Toast.makeText(this, "Gracias por tu calificación", Toast.LENGTH_SHORT).show()
                    }, { errorMsg ->
                        Toast.makeText(this, "Error al guardar calificación: $errorMsg", Toast.LENGTH_SHORT).show()
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}

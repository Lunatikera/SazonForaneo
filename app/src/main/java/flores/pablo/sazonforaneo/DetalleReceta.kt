package flores.pablo.sazonforaneo

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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


    private lateinit var receta: Receta

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

        receta = intent.getSerializableExtra("receta") as Receta

        mostrarDatos(receta)

        configurarTabs()
    }

    private fun mostrarDatos(receta: Receta) {
        tvTitulo.text = receta.nombre

        Glide.with(this)
            .load(receta.imagenUriString)
            .placeholder(R.drawable.pizza)
            .into(ivReceta)

        tvAutor.text = receta.autor
        tvCalificacion.text = String.format("%.1f", receta.rating)
        tvDescripcion.text = receta.descripcion
        tvFuente.text = receta.fuente

        layoutTags.removeAllViews()
        val allTags = receta.categorias + receta.etiquetas
        for (tag in allTags) {
            val tagView = TextView(this).apply {
                text = tag
                setPadding(24, 12, 24, 12)
                setTextColor(android.graphics.Color.WHITE)
                setBackgroundResource(R.drawable.tag_green_background)
                val params = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 16, 16)
                layoutParams = params
            }
            layoutTags.addView(tagView)
        }

        tvContenidoReceta.text = receta.ingredientes.joinToString(separator = "\n") { "• $it" }
    }

    private fun configurarTabs() {
        tabIngredientes.setOnClickListener {
            tabIngredientes.setBackgroundResource(R.drawable.tab_unselected)
            tabIngredientes.setTextColor(resources.getColor(android.R.color.black))

            tabInstrucciones.setBackgroundResource(R.drawable.tab_selected)
            tabInstrucciones.setTextColor(resources.getColor(android.R.color.darker_gray))

            tvContenidoReceta.text = receta.ingredientes.joinToString(separator = "\n") { "• $it" }
        }

        tabInstrucciones.setOnClickListener {
            tabInstrucciones.setBackgroundResource(R.drawable.tab_unselected)
            tabInstrucciones.setTextColor(resources.getColor(android.R.color.black))

            tabIngredientes.setBackgroundResource(R.drawable.tab_selected)
            tabIngredientes.setTextColor(resources.getColor(android.R.color.darker_gray))

            tvContenidoReceta.text = receta.instrucciones
        }
    }
}
package flores.pablo.sazonforaneo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout

class AgregarVisibilidad : AppCompatActivity() {
    private lateinit var receta: Receta
    private lateinit var layoutCategorias: FlexboxLayout
    private lateinit var etEtiqueta: EditText
    private lateinit var radioGroupPrivacidad: RadioGroup
    private lateinit var recyclerEtiquetas: RecyclerView
    private lateinit var etiquetaAdapter: EtiquetaAdapter

    private val etiquetas = mutableListOf<String>()
    private val categoriasSeleccionadas = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_visibilidad)

        receta = intent.getSerializableExtra("receta") as? Receta ?: Receta("", "")

        layoutCategorias = findViewById(R.id.layoutCategorias)
        etEtiqueta = findViewById(R.id.etEtiqueta)
        radioGroupPrivacidad = findViewById(R.id.radioGroupPrivacidad)
        recyclerEtiquetas = findViewById(R.id.recyclerEtiquetas)

        // Inicializar adapter con callback para eliminar etiqueta
        etiquetaAdapter = EtiquetaAdapter(etiquetas) { etiquetaEliminada ->
            etiquetas.remove(etiquetaEliminada)
            etiquetaAdapter.notifyDataSetChanged()
        }

        recyclerEtiquetas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerEtiquetas.adapter = etiquetaAdapter

        val btnAgregarEtiqueta = findViewById<Button>(R.id.btnAgregarEtiqueta)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        cargarCategorias()

        btnAgregarEtiqueta.setOnClickListener {
            val etiqueta = etEtiqueta.text.toString().trim()
            if (etiqueta.isNotEmpty() && !etiquetas.contains(etiqueta)) {
                agregarEtiqueta(etiqueta)
                etEtiqueta.text.clear()
            }
        }

        btnContinuar.setOnClickListener {
            val visibilidad = when (radioGroupPrivacidad.checkedRadioButtonId) {
                R.id.rbPublica -> "publico"
                R.id.rbPrivada -> "privada"
                else -> "publico"
            }

            receta.visibilidad = visibilidad
            receta.etiquetas = etiquetas.toList()
            receta.categorias = categoriasSeleccionadas.toList()

            val intent = Intent(this, AgregarIngredientes::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }
    }

    private fun cargarCategorias() {
        val categorias = listOf("Entradas", "Sopas", "Ensaladas", "Postres", "Snacks", "Picante", "Dulce", "Salado")

        layoutCategorias.removeAllViews()

        for (categoria in categorias) {
            val chip = TextView(this).apply {
                text = categoria
                setPadding(32, 16, 32, 16)
                setTextColor(Color.parseColor("#44291D")) // Marrón oscuro para no seleccionado
                setBackgroundResource(R.drawable.chip_categoria)
                textSize = 14f
                setOnClickListener {
                    toggleCategoria(categoria, this)
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 8, 8, 8)
            chip.layoutParams = params

            layoutCategorias.addView(chip)
        }
    }

    private fun toggleCategoria(categoria: String, view: TextView) {
        if (categoriasSeleccionadas.contains(categoria)) {
            categoriasSeleccionadas.remove(categoria)
            view.setBackgroundResource(R.drawable.chip_categoria)
            view.setTextColor(Color.parseColor("#44291D")) // Marrón oscuro para no seleccionado
        } else {
            categoriasSeleccionadas.add(categoria)
            view.setBackgroundResource(R.drawable.chip_categoria_selected)
            view.setTextColor(Color.parseColor("#FFFFFF")) // Blanco para seleccionado
        }
    }

    private fun agregarEtiqueta(etiqueta: String) {
        etiquetas.add(etiqueta)
        etiquetaAdapter.notifyItemInserted(etiquetas.size - 1)
    }
}
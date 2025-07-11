package flores.pablo.sazonforaneo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import flores.pablo.sazonforaneo.ui.Receta

class AgregarVisibilidad : AppCompatActivity() {
    private lateinit var receta: Receta
    private lateinit var layoutCategorias: FlexboxLayout
    private lateinit var etEtiqueta: EditText
    private lateinit var radioGroupPrivacidad: RadioGroup
    private lateinit var recyclerEtiquetas: RecyclerView
    private lateinit var etiquetaAdapter: EtiquetaAdapter
    private lateinit var tvTitulo: TextView

    private val etiquetas = mutableListOf<String>()
    private val categoriasSeleccionadas = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_visibilidad)

        // recuperar la receta (que puede ser nueva o para editar)
        receta = intent.getSerializableExtra("receta") as? Receta ?: Receta() // se asegura de tener un objeto Receta

        layoutCategorias = findViewById(R.id.layoutCategorias)
        etEtiqueta = findViewById(R.id.etEtiqueta)
        radioGroupPrivacidad = findViewById(R.id.radioGroupPrivacidad)
        recyclerEtiquetas = findViewById(R.id.recyclerEtiquetas)
        tvTitulo = findViewById(R.id.tvTitulo)


        etiquetaAdapter = EtiquetaAdapter(etiquetas) { etiquetaEliminada ->
            etiquetas.remove(etiquetaEliminada)
        }

        recyclerEtiquetas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerEtiquetas.adapter = etiquetaAdapter

        val btnAgregarEtiqueta = findViewById<Button>(R.id.btnAgregarEtiqueta)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        cargarCategorias() // llama primero para crear los chips


        // cambiar titulo si estamos en modo edicion
        if (receta.id.isNotEmpty()) {
            tvTitulo.text = "Editar Receta"
        } else {
            tvTitulo.text = "Nueva Receta"
        }


        //  precargar los datos si estamos editando
        if (receta.id.isNotEmpty()) { // si la receta ya tiene un ID, estamos editando
            // precargar categorias
            for (categoriaReceta in receta.categorias) {
                toggleCategoria(categoriaReceta, findChipForCategory(categoriaReceta))
            }

            // precargar etiquetas
            etiquetas.addAll(receta.etiquetas)
            etiquetaAdapter.notifyDataSetChanged()

            // precargar visibilidad
            when (receta.visibilidad) {
                "publico" -> findViewById<RadioButton>(R.id.rbPublica).isChecked = true
                "privada" -> findViewById<RadioButton>(R.id.rbPrivada).isChecked = true
            }
        }


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
                else -> "publico" // Valor por defecto
            }

            receta.visibilidad = visibilidad
            receta.etiquetas = etiquetas.toList() // actualizar etiquetas en el objeto
            receta.categorias = categoriasSeleccionadas.toList() // actualizar categorias en el objeto

            val intent = Intent(this, AgregarIngredientes::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }
    }

    private fun cargarCategorias() {
        val categorias = listOf(
            "Entradas",
            "Sopas",
            "Platos Fuertes",
            "Ensaladas",
            "Guarniciones",
            "Postres",
            "Mariscos",
            "Desayunos",
            "Bebidas",
            "Salsas y Aderezos",
            "Panadería",
            "Pastas",
            "Comida Internacional",
            "Vegetariana/Vegana",
            "Rápidas y Fáciles",
            "Antojitos Mexicanos"
        )

        layoutCategorias.removeAllViews()

        for (categoria in categorias) {
            val chip = TextView(this).apply {
                text = categoria
                setPadding(32, 16, 32, 16)
                setTextColor(Color.parseColor("#44291D"))
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

    // para encontrar el chip de una categoria
    private fun findChipForCategory(categoryName: String): TextView? {
        for (i in 0 until layoutCategorias.childCount) {
            val chip = layoutCategorias.getChildAt(i) as? TextView
            if (chip?.text.toString() == categoryName) {
                return chip
            }
        }
        return null
    }

    private fun toggleCategoria(categoria: String, view: TextView?) {
        if (view == null) return // si no se encuentra la vista, salimos

        if (categoriasSeleccionadas.contains(categoria)) {
            categoriasSeleccionadas.remove(categoria)
            view.setBackgroundResource(R.drawable.chip_categoria)
            view.setTextColor(Color.parseColor("#44291D"))
        } else {
            categoriasSeleccionadas.add(categoria)
            view.setBackgroundResource(R.drawable.chip_categoria_selected)
            view.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun agregarEtiqueta(etiqueta: String) {
        etiquetas.add(etiqueta)
        etiquetaAdapter.notifyItemInserted(etiquetas.size - 1)
    }
}
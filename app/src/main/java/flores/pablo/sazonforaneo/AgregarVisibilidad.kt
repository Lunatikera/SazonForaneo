package flores.pablo.sazonforaneo

import android.content.Intent
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
import com.google.android.flexbox.FlexboxLayout

class AgregarVisibilidad : AppCompatActivity() {

    private lateinit var receta: Receta
    private lateinit var layoutEtiquetas: LinearLayout
    private lateinit var layoutCategorias: FlexboxLayout
    private lateinit var etEtiqueta: EditText
    private lateinit var radioGroupPrivacidad: RadioGroup
    private val etiquetas = mutableListOf<String>()
    private val categoriasSeleccionadas = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_visibilidad)

        receta = intent.getSerializableExtra("receta") as? Receta ?: Receta("", "")

        layoutEtiquetas = findViewById(R.id.layoutEtiquetas)
        layoutCategorias = findViewById(R.id.layoutCategorias)
        etEtiqueta = findViewById(R.id.etEtiqueta)
        radioGroupPrivacidad = findViewById(R.id.radioGroupPrivacidad)

        val btnAgregarEtiqueta = findViewById<Button>(R.id.btnAgregarEtiqueta)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        cargarCategorias()

        btnAgregarEtiqueta.setOnClickListener {
            val etiqueta = etEtiqueta.text.toString().trim()
            if (etiqueta.isNotEmpty()) {
                agregarEtiqueta(etiqueta)
                etEtiqueta.text.clear()
            }
        }

        btnContinuar.setOnClickListener {
            val visibilidad = when (radioGroupPrivacidad.checkedRadioButtonId) {
                R.id.rbPublica -> "publica"
                R.id.rbPrivada -> "privada"
                else -> "publica"
            }

            receta.visibilidad = visibilidad
            receta.etiquetas = etiquetas
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
                setTextColor(resources.getColor(android.R.color.white))
                setBackgroundResource(R.drawable.tag_green_background)
                setTextSize(14f)
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
            view.alpha = 0.5f
        } else {
            categoriasSeleccionadas.add(categoria)
            view.alpha = 1.0f
        }
    }

    private fun agregarEtiqueta(etiqueta: String) {
        if (!etiquetas.contains(etiqueta)) {
            etiquetas.add(etiqueta)
            val chip = TextView(this).apply {
                text = etiqueta
                setPadding(32, 16, 32, 16)
                setTextColor(resources.getColor(android.R.color.white))
                setBackgroundResource(R.drawable.tag_green_background)
                setTextSize(14f)
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            chip.layoutParams = params

            layoutEtiquetas.addView(chip)
        }
    }
}
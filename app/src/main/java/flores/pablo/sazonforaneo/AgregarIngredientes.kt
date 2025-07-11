package flores.pablo.sazonforaneo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarIngredientes : AppCompatActivity() {
    private lateinit var receta: Receta
    private lateinit var etIngrediente: EditText
    private lateinit var btnAgregar: Button
    private lateinit var listView: ListView
    private lateinit var btnContinuar: Button

    private lateinit var adapter: IngredientesAdapter
    private val ingredientes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ingredientes)

        // recuperar la receta (que puede ser nueva o para editar)
        receta = intent.getSerializableExtra("receta") as? Receta ?: Receta() // se asegura de tener un objeto Receta

        etIngrediente = findViewById(R.id.etIngrediente)
        btnAgregar = findViewById(R.id.btnAgregarIngrediente)
        listView = findViewById(R.id.listViewIngredientes)
        btnContinuar = findViewById(R.id.btnContinuar)

        adapter = IngredientesAdapter(this, ingredientes)
        listView.adapter = adapter

        // precargar datos si estamos editando
        if (receta.id.isNotEmpty()) {  // si la receta ya tiene un ID, estamos editando
            ingredientes.addAll(receta.ingredientes)
            adapter.notifyDataSetChanged()
        }

        btnAgregar.setOnClickListener {
            val texto = etIngrediente.text.toString().trim()
            if (texto.isNotEmpty()) {
                ingredientes.add(texto)
                adapter.notifyDataSetChanged()
                etIngrediente.text.clear()
            }
        }

        btnContinuar.setOnClickListener {
            receta.ingredientes = ingredientes // actualizar ingredientes en el objeto
            val intent = Intent(this, AgregarInstrucciones::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }
    }
}
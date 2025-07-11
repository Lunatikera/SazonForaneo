package flores.pablo.sazonforaneo.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.RecetaViewModel
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.ui.destacadas.RecetasDestacadasAdapter

class RecetasDestacadasActivity : AppCompatActivity() {

    private val viewModel: RecetaViewModel by viewModels()
    private lateinit var adapter: RecetasDestacadasAdapter
    private val usuarioRepo = UsuarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recetas_destacadas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = RecetasDestacadasAdapter(usuarioRepo) { receta ->

        }

        val recyclerView = findViewById<RecyclerView>(R.id.recipes_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.recetas.observe(this, Observer { lista ->
            adapter.actualizarLista(lista)
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let {
                 Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        })

        // Carga recetas destacadas al iniciar
        viewModel.cargarRecetasDestacadas()
    }
}

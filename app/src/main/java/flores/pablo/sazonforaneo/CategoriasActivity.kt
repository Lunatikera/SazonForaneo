package flores.pablo.sazonforaneo
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriasActivity : AppCompatActivity() {

    private lateinit var categoriasAdapter: CategoriaAdapter
    private val categorias = listOf(
        Categoria("Entradas", R.drawable.sazon_logo),
        Categoria("Sopas", R.drawable.sazon_logo),
        Categoria("Platos fuertes", R.drawable.sazon_logo),
        Categoria("Ensaladas", R.drawable.sazon_logo),
        Categoria("Guarniciones", R.drawable.sazon_logo),
        Categoria("Postres", R.drawable.sazon_logo),
        Categoria("Snacks", R.drawable.sazon_logo),
        Categoria("Salsas", R.drawable.sazon_logo)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        val ivPerfil = findViewById<ImageView>(R.id.ivPerfil)
        val btnCategoria = findViewById<Button>(R.id.btnCategoria)
        val btnEtiquetas = findViewById<Button>(R.id.btnEtiquetas)
        val recycler = findViewById<RecyclerView>(R.id.recyclerCategorias)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        categoriasAdapter = CategoriaAdapter(categorias) { categoria ->
            val intent = Intent(this, RecetasPorCategoriaActivity::class.java)
            intent.putExtra("categoria", categoria.nombre)
            startActivity(intent)
        }

        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = categoriasAdapter

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtradas = categorias.filter {
                    it.nombre.contains(s.toString(), ignoreCase = true)
                }
                categoriasAdapter.actualizarLista(filtradas)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        ivPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilUsuarioActivity::class.java))
        }

        btnCategoria.setOnClickListener {
            // Ya estás aquí, puedes dejarlo sin acción
        }

        btnEtiquetas.setOnClickListener {
            startActivity(Intent(this, EtiquetasActivity::class.java))
        }

        fabAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarRecetaActivity::class.java))
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_explorar -> startActivity(Intent(this, ExplorarActivity::class.java))
                R.id.nav_mis_recetas -> startActivity(Intent(this, MisRecetasActivity::class.java))
                R.id.nav_perfil -> startActivity(Intent(this, PerfilUsuarioActivity::class.java))
            }
            true
        }
    }
}
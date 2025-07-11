package flores.pablo.sazonforaneo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import flores.pablo.sazonforaneo.AgregarNombreDescripcion
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta

class ExplorarActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    // Registrar launcher para recibir resultado de AgregarNombreDescripcion
    private val agregarRecetaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val nuevaReceta = result.data?.getSerializableExtra("nuevaReceta") as? Receta
                nuevaReceta?.let {
                    // Navegar al fragmento Explorar pasando la receta nueva en bundle
                    val bundle = Bundle().apply {
                        putSerializable("nuevaReceta", it)
                    }
                    navController.navigate(R.id.nav_explorar, bundle)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val fabAddRecipe = findViewById<FloatingActionButton>(R.id.fab_add_recipe)
        val fabDestacadas= findViewById<FloatingActionButton>(R.id.fab_favorite)

        // Detectar si viene receta nueva directamente en el intent (por ejemplo desde AgregarImagenFuente)
        val nuevaReceta = intent.getSerializableExtra("nuevaReceta") as? Receta
        if (nuevaReceta != null) {
            val bundle = Bundle().apply {
                putSerializable("nuevaReceta", nuevaReceta)
            }
            navController.navigate(R.id.nav_explorar, bundle)
            bottomNav.selectedItemId = R.id.nav_explorar
        } else {
            val fragmentToShow = intent.getStringExtra("fragment_to_show")
            when (fragmentToShow) {
                "mis_recetas" -> {
                    navController.navigate(R.id.nav_mis_recetas)
                    bottomNav.selectedItemId = R.id.nav_mis_recetas
                }
                "explorar" -> {
                    navController.navigate(R.id.nav_explorar)
                    bottomNav.selectedItemId = R.id.nav_explorar
                }
                else -> {
                    navController.navigate(R.id.nav_categorias)
                    bottomNav.selectedItemId = R.id.nav_categorias
                }
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_categorias -> {
                    navController.navigate(R.id.nav_categorias)
                    true
                }
                R.id.nav_explorar -> {
                    navController.navigate(R.id.nav_explorar)
                    true
                }
                R.id.nav_mis_recetas -> {
                    navController.navigate(R.id.nav_mis_recetas)
                    true
                }
                else -> false
            }
        }

        fabAddRecipe.setOnClickListener {
            val intent = Intent(this, AgregarNombreDescripcion::class.java)
            agregarRecetaLauncher.launch(intent)
        }

        fabDestacadas.setOnClickListener {
            val intent = Intent(this, RecetasDestacadasActivity::class.java)
            startActivity(intent)
        }
    }
}

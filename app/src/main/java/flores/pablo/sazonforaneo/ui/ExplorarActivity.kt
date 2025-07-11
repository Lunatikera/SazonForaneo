package flores.pablo.sazonforaneo.ui

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val fabAddRecipe = findViewById<FloatingActionButton>(R.id.fab_add_recipe)

        // Detectar si viene receta nueva en el intent
        val nuevaReceta = intent.getSerializableExtra("nuevaReceta") as? Receta

        // Navegar al fragmento Explorar y pasar la receta nueva si existe
        if (nuevaReceta != null) {
            val bundle = Bundle().apply {
                putSerializable("nuevaReceta", nuevaReceta)
            }
            navController.navigate(R.id.nav_explorar, bundle)
            bottomNav.selectedItemId = R.id.nav_explorar
        } else {
            // Navegación inicial según intent o por defecto
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

        // Escuchar selección del BottomNavigationView
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
            startActivity(intent)
        }
    }
}

package flores.pablo.sazonforaneo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.ui.categorias.CategoriasFragment
import flores.pablo.sazonforaneo.ui.explorar.ExplorarFragment
import flores.pablo.sazonforaneo.ui.recetas.MisRecetasFragment

class ExplorarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorar)

        replaceFragment(CategoriasFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        bottomNav.selectedItemId = R.id.nav_categorias

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_categorias -> {
                    replaceFragment(CategoriasFragment())
                    true
                }
                R.id.nav_explorar -> {
                    replaceFragment(ExplorarFragment())
                    true
                }
                R.id.nav_mis_recetas -> {
                    replaceFragment(MisRecetasFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }
}

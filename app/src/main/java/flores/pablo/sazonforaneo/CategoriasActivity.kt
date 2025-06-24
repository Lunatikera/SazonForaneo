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
import flores.pablo.sazonforaneo.databinding.ActivityCategoriasBinding

class CategoriasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarListenersDeClic()
        configurarBarraNavegacionInferior()
    }

    private fun configurarRecyclerView() {
        val categorias = listOf(
            Categoria("Entradas", R.drawable.imagen_entradas),
            Categoria("Sopas", R.drawable.imagen_sopas),
            Categoria("Platos Fuertes", R.drawable.imagen_platos_fuertes),
            Categoria("Ensaladas", R.drawable.imagen_ensaladas),
            Categoria("Guarniciones", R.drawable.imagen_guarniciones),
            Categoria("Postres", R.drawable.imagen_postres),
            Categoria("Snacks", R.drawable.imagen_snacks),
            Categoria("Salsas", R.drawable.imagen_salsas)
        )

        val adaptadorCategorias = CategoriaAdapter(categorias) { categoria ->
            // cuando se da click en  alguna categoria
            Toast.makeText(this, "Categoría: ${categoria.nombre}", Toast.LENGTH_SHORT).show()
            // aqui para navegar a una nueva pantalla para mostrar recetas de esa categoria
        }

        binding.categoriesGridRecyclerview.apply {
            layoutManager = GridLayoutManager(this@CategoriasActivity, 2)
            adapter = adaptadorCategorias
        }
    }

    private fun configurarListenersDeClic() {
        // Clic en la imagen de perfil para ir a activity_profile (aún no existe)
        binding.ivPerfil.setOnClickListener {
            Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            //AQUI SE AÑADIRA LA LOGICA DEL INTENT PARA MANDARTE A LA PANTALLA DE PERFIL
            // startActivity(intent)
        }

        // clic en el botón flotante para añadir receta
        binding.fabAddRecipe.setOnClickListener {
            Toast.makeText(this, "Añadir nueva receta", Toast.LENGTH_SHORT).show()
            //AQUI SE AÑADIRA LA LOGICA PARA MANDARTE A LA PANTALLA DE AÑADIR RECETA
            // startActivity(intent)

        }

        // clic en el boton Etiquetas (por ahora no hace nadaa)
        binding.tagsButton.setOnClickListener {
            Toast.makeText(this, "Botón Etiquetas (funcionalidad pendiente)", Toast.LENGTH_SHORT).show()
        }


    }

    private fun configurarBarraNavegacionInferior() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explorar -> {
                    // Ya estás en Explorar (CategoriasActivity), no hacer nada o refrescar
                    Toast.makeText(this, "Sección Explorar", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_mis_recetas -> {
                    // Aquí para ir a la actividad de "Mis Recetas"
                    Toast.makeText(this, "Sección Mis Recetas", Toast.LENGTH_SHORT).show()
                    //AQUI SE AÑADIRA LA LOGICA DEL INTENT PARA MANDARTE A LA PANTALLA DE MIS RECETAS
                    // startActivity(intent)
                    true
                }
                R.id.nav_categorias -> {

                    Toast.makeText(this, "Sección Categorías", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        // para que el item "Categorias" aparezca seleccionado por defecto al iniciar la actividad de las categoriasss
        binding.bottomNavigationView.selectedItemId = R.id.nav_categorias
    }
}
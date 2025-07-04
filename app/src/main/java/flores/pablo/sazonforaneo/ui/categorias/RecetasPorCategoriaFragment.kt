package flores.pablo.sazonforaneo.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.ui.explorar.ExplorarAdapter
import androidx.navigation.fragment.navArgs
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment

class RecetasPorCategoriaFragment : Fragment() {

    private lateinit var tvTituloCategoria: TextView
    private lateinit var etBuscar: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExplorarAdapter
    private lateinit var ivPerfil: ImageView
    private lateinit var tagsButton: Button

    private val args: RecetasPorCategoriaFragmentArgs by navArgs()
    private var allRecetasForCategory: List<Receta> = emptyList()
    private var selectedTags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_recetas_por_categoria, container, false)

        tvTituloCategoria = view.findViewById(R.id.tvTituloCategoria)
        etBuscar = view.findViewById(R.id.etBuscar)
        recyclerView = view.findViewById(R.id.recyclerview_recetas_categoria)
        ivPerfil = view.findViewById(R.id.ivPerfil)
        tagsButton = view.findViewById(R.id.tags_button)

        val nombreCategoria = args.categoriaNombre
        tvTituloCategoria.text = "Recetas de $nombreCategoria"

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val allMockRecetas = getMockRecetas()
        allRecetasForCategory = allMockRecetas.filter { receta -> receta.categorias.contains(nombreCategoria) }

        adapter = ExplorarAdapter(allRecetasForCategory) { receta ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        ivPerfil.setOnClickListener {
            val intent = Intent(requireContext(), PerfilConfigActivity::class.java)
            startActivity(intent)
        }

        tagsButton.setOnClickListener {
            val dialog = TagsDialogFragment(selectedTags) { tags ->
                selectedTags = tags.toMutableList()
                filtrarRecetasPorTags(selectedTags)
            }
            dialog.show(childFragmentManager, "TagsDialogRecetasCategoria")
        }

       //AQUI SE PUEDE PONER LA LOGICA PARA LA BARR ADE BUSQUEDA

        return view
    }

    // Función para filtrar recetas por tags
    private fun filtrarRecetasPorTags(tags: List<String>) {
        if (tags.isEmpty()) {
            // Si no hay tags seleccionados, muestra todas las recetas de la categoría
            adapter.actualizarLista(allRecetasForCategory)
        } else {
            // Filtra las recetas de la categoría que contengan *todos* los tags seleccionados
            val filtradas = allRecetasForCategory.filter { receta ->
                tags.all { tag -> receta.etiquetas.contains(tag) }
            }
            adapter.actualizarLista(filtradas)
        }
    }


    private fun getMockRecetas(): List<Receta> {
        return listOf(
            Receta(
                nombre = "Pizza Margherita",
                descripcion = "Pizza clásica italiana con salsa de tomate, mozzarella y albahaca fresca.",
                categorias = listOf("Platos Fuertes", "Entradas"),
                etiquetas = listOf("Pizza", "Margherita", "Vegetariana"),
                visibilidad = "Pública",
                ingredientes = listOf("Masa para pizza", "Salsa de tomate", "Mozzarella", "Albahaca fresca", "Aceite de oliva", "Sal"),
                instrucciones = "Extender la masa, cubrir con salsa, mozzarella y albahaca. Hornear a 250°C por 10-12 minutos.",
                fuente = "Recetas Clásicas",
                autor = "Marco Bianchi",
                rating = 4.7f,
            ),

            Receta(
                nombre = "Tacos al Pastor",
                descripcion = "Tacos tradicionales mexicanos con carne marinada y piña.",
                categorias = listOf("Salsas", "Entradas", "Platos Fuertes"), // Corregí 'Plato Fuerte' a 'Platos Fuertes' para consistencia
                etiquetas = listOf("Tacos", "Pastor", "Cena"),
                visibilidad = "Pública",
                ingredientes = listOf("Carne de cerdo", "Piña", "Tortillas", "Achiote", "Cebolla", "Cilantro"),
                instrucciones = "Marinar la carne, cocinar en trompo, servir en tortilla con piña, cebolla y cilantro.",
                fuente = "Recetario Popular",
                autor = "Chef Luis Hernández",
                rating = 4.5f,
                imagenUriString = "https://assets.tmecosys.com/image/upload/t_web_rdp_recipe_584x480/img/recipe/ras/Assets/C07AE049-11C3-4672-A96A-A547C15F0116/Derivates/FE1D05A4-0A44-4007-9A42-5CAFD9F8F798.jpg"
            ),

            Receta(
                nombre = "Spaghetti Carbonara",
                descripcion = "Receta italiana cremosa con tocino y queso.",
                categorias = listOf("Salsas", "Platos Fuertes","Guarniciones"), // Corregí 'Plato Fuerte' a 'Platos Fuertes'
                etiquetas = listOf("Spaghetti", "Carbonara", "Cena"),
                visibilidad = "Pública",
                ingredientes = listOf("Pasta", "Huevo", "Queso parmesano", "Pimienta", "Tocino"),
                instrucciones = "Cocer la pasta, mezclar con huevos y queso, añadir el tocino dorado.",
                fuente = "Cocina Italiana",
                autor = "Giovanna Rossi",
                rating = 4.8f,
                imagenUriString = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTocnT-UeCFm5CnI92RPn7zFsCJMH2AEW60vA&s"
            ),

            Receta(
                nombre = "Ensalada César",
                descripcion = "Clásica ensalada con lechuga romana, aderezo césar y crutones.Clásica ensalada con lechuga romana, aderezo césar y crutones",
                categorias = listOf("Guarniciones", "Snacks","Ensaladas"),
                etiquetas = listOf("Ensalada", "César", "Lechuga"),
                visibilidad = "Privada",
                ingredientes = listOf("Lechuga", "Pollo", "Crutones", "Aderezo César", "Queso parmesano"),
                instrucciones = "Mezclar ingredientes frescos y servir con aderezo.",
                fuente = "Blog de Cocina Saludable",
                autor = "Ana Gómez",
                rating = 3.9f,
                imagenUriString = "https://www.gourmet.cl/wp-content/uploads/2016/09/EnsaladaCesar2.webp"
            )
        )
    }
}
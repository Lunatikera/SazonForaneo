package flores.pablo.sazonforaneo.ui.explorar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import flores.pablo.sazonforaneo.AgregarNombreDescripcion
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.databinding.FragmentExplorarBinding
import flores.pablo.sazonforaneo.Receta

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExplorarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recetas = obtenerRecetasEjemplo()

        adapter = ExplorarAdapter(recetas) { recetaSeleccionada ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", recetaSeleccionada)
            startActivity(intent)
        }

        binding.recipesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerview.adapter = adapter

        binding.ivPerfil.setOnClickListener {
            val intent = Intent(requireContext(), PerfilConfigActivity::class.java)
            startActivity(intent)
        }
        binding.fabAddRecipe.setOnClickListener {
            val intent = Intent(requireContext(), AgregarNombreDescripcion::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerRecetasEjemplo(): List<Receta> {
        return listOf(
            Receta(
                nombre = "Pizza Margherita",
                descripcion = "Pizza clásica italiana con salsa de tomate, mozzarella y albahaca fresca.",
                categorias = listOf("Italiana", "Pizza"),
                etiquetas = listOf("Pizza", "Margherita", "Vegetariana"),
                visibilidad = "Pública",
                ingredientes = listOf("Masa para pizza", "Salsa de tomate", "Mozzarella", "Albahaca fresca", "Aceite de oliva", "Sal"),
                instrucciones = "Extender la masa, cubrir con salsa, mozzarella y albahaca. Hornear a 250°C por 10-12 minutos.",
                fuente = "Recetas Clásicas",
                autor = "Marco Bianchi",
                rating = 4.7f,
            ),

            Receta(nombre = "Tacos al Pastor",
        descripcion = "Tacos tradicionales mexicanos con carne marinada y piña.",
        categorias = listOf("Mexicana", "Callejera"),
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
            categorias = listOf("Italiana", "Pasta"),
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
            descripcion = "Clásica ensalada con lechuga romana, aderezo césar y crutones.",
            categorias = listOf("Saludable", "Entrada"),
            etiquetas = listOf("Ensalada", "César", "Lechuga"),
            visibilidad = "Privada",
            ingredientes = listOf("Lechuga", "Pollo", "Crutones", "Aderezo César", "Queso parmesano"),
            instrucciones = "Mezclar ingredientes frescos y servir con aderezo.",
            fuente = "Blog de Cocina Saludable",
            autor = "Ana Gómez",
            rating = 3.9f,
            imagenUriString = "https://www.gourmet.cl/wp-content/uploads/2016/09/EnsaladaCesar2.webp"
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
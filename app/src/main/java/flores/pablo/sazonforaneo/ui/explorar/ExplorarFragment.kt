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
import flores.pablo.sazonforaneo.PerfilConfigActivity
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
                nombre = "Pizza estilo italiana",
                descripcion = "Pizza tradicional con albahaca y queso mozzarella.",
                categorias = listOf("Italiana"),
                etiquetas = listOf("Fácil"),
                fuente = "Ana Perez",
                imagenUriString  = "https://example.com/pizza.jpg"
            ),
            Receta(
                nombre = "Pizza estilo italiana",
                descripcion = "Pizza tradicional con albahaca y queso mozzarella.",
                categorias = listOf("Italiana"),
                etiquetas = listOf("Fácil"),
                fuente = "Ana Perez",
                imagenUriString  = "https://example.com/pizza.jpg"
            ), Receta(
                nombre = "Pizza estilo italiana",
                descripcion = "Pizza tradicional con albahaca y queso mozzarella.",
                categorias = listOf("Italiana"),
                etiquetas = listOf("Fácil"),
                fuente = "Ana Perez",
                imagenUriString  = "https://example.com/pizza.jpg"
            ), Receta(
                nombre = "Pizza estilo italiana",
                descripcion = "Pizza tradicional con albahaca y queso mozzarella.",
                categorias = listOf("Italiana"),
                etiquetas = listOf("Fácil"),
                fuente = "Ana Perez",
                imagenUriString  = "https://example.com/pizza.jpg"
            ), Receta(
                nombre = "Pizza estilo italiana",
                descripcion = "Pizza tradicional con albahaca y queso mozzarella.",
                categorias = listOf("Italiana"),
                etiquetas = listOf("Fácil"),
                fuente = "Ana Perez",
                imagenUriString  = "https://example.com/pizza.jpg"
            ),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
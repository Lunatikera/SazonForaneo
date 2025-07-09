package flores.pablo.sazonforaneo.ui.explorar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.RecetaViewModel
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.databinding.FragmentExplorarBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExplorarAdapter
    private var allRecetas = listOf<Receta>()
    private var selectedTags = mutableListOf<String>()

    private val viewModel: RecetaViewModel by viewModels()
    private val usuarioRepo = UsuarioRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExplorarAdapter(emptyList(), usuarioRepo) { receta ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }

        binding.recipesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerview.adapter = adapter

        viewModel.recetas.observe(viewLifecycleOwner, Observer { lista ->
            allRecetas = lista
            filtrarRecetasPorTags(selectedTags)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                allRecetas = obtenerRecetasEjemplo()
                filtrarRecetasPorTags(selectedTags)
            }
        })

        viewModel.cargarRecetas()

        usuarioRepo.obtenerUsuarioActual { usuario ->
            if (usuario == null) {
                Log.d("ExplorarFragment", "Usuario es null")
            } else {
                Log.d("ExplorarFragment", "Usuario imagenPerfil: ${usuario.imagenPerfil}")
            }
            val imagenPerfilUrl = usuario?.imagenPerfil
            if (!imagenPerfilUrl.isNullOrEmpty()) {
                Glide.with(this@ExplorarFragment)  // contexto explícito del Fragment
                    .load(imagenPerfilUrl)
                    .placeholder(R.drawable.imagen_predeterminada)
                    .circleCrop()
                    .error(R.drawable.imagen_predeterminada)
                    .into(binding.ivPerfil)
            } else {
                binding.ivPerfil.setImageResource(R.drawable.imagen_predeterminada)
            }
        }

        binding.ivPerfil.setOnClickListener {
            startActivity(Intent(requireContext(), PerfilConfigActivity::class.java))
        }

        binding.tagsButton.setOnClickListener {
            TagsDialogFragment(
                initialTags = selectedTags,
                initialCategories = emptyList()
            ) { tags, _ ->
                selectedTags = tags.toMutableList()
                filtrarRecetasPorTags(selectedTags)
            }.show(childFragmentManager, "TagsDialogExplorar")
        }
    }

    private fun filtrarRecetasPorTags(tags: List<String>) {
        val lista = if (tags.isEmpty()) {
            allRecetas
        } else {
            allRecetas.filter { receta -> tags.all { tag -> receta.etiquetas.contains(tag) } }
        }
        adapter.actualizarLista(lista)
    }

    private fun obtenerRecetasEjemplo(): List<Receta> = listOf(
        Receta(
            nombre = "Pizza Margherita",
            descripcion = "Pizza clásica italiana con salsa de tomate, mozzarella y albahaca fresca.",
            categorias = listOf("Italiana", "Pizza"),
            etiquetas = listOf("Pizza", "Margherita", "Vegetariana"),
            visibilidad = "Pública",
            ingredientes = listOf("Masa para pizza", "Salsa de tomate", "Mozzarella", "Albahaca fresca", "Aceite de oliva", "Sal"),
            instrucciones = "Extender la masa, cubrir con salsa, mozzarella y albahaca. Hornear a 250°C por 10-12 minutos.",
            fuente = "Recetas Clásicas",
            rating = 4.7f,
        ),
        Receta(
            nombre = "Tacos al Pastor",
            descripcion = "Tacos tradicionales mexicanos con carne marinada y piña.",
            categorias = listOf("Mexicana", "Callejera"),
            etiquetas = listOf("Tacos", "Pastor", "Cena"),
            visibilidad = "Pública",
            ingredientes = listOf("Carne de cerdo", "Piña", "Tortillas", "Achiote", "Cebolla", "Cilantro"),
            instrucciones = "Marinar la carne, cocinar en trompo, servir en tortilla con piña, cebolla y cilantro.",
            fuente = "Recetario Popular",
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
            rating = 3.9f,
            imagenUriString = "https://www.gourmet.cl/wp-content/uploads/2016/09/EnsaladaCesar2.webp"
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

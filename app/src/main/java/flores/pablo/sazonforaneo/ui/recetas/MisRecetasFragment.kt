package flores.pablo.sazonforaneo.ui.recetas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import flores.pablo.sazonforaneo.AgregarNombreDescripcion
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.databinding.FragmentMisrecetasBinding
import flores.pablo.sazonforaneo.Receta

class MisRecetasFragment : Fragment() {

    private var _binding: FragmentMisrecetasBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MisRecetasAdapter
    private val usuarioRepo = UsuarioRepository()  // Instancia para pasar al adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisrecetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val misRecetas = obtenerMisRecetasEjemplo()

        adapter = MisRecetasAdapter(misRecetas, usuarioRepo) { recetaSeleccionada ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", recetaSeleccionada)
            startActivity(intent)
        }

        binding.rvMisRecetas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMisRecetas.adapter = adapter
    }

    private fun obtenerMisRecetasEjemplo(): List<Receta> {
        return listOf(
            Receta(
                nombre = "Spaghetti Carbonara",
                descripcion = "Receta italiana cremosa con tocino y queso.",
                categorias = listOf("Italiana", "Pasta"),
                etiquetas = listOf("Spaghetti", "Carbonara", "Cena"),
                visibilidad = "Pública",
                ingredientes = listOf("Pasta", "Huevo", "Queso parmesano", "Pimienta", "Tocino"),
                instrucciones = "Cocer la pasta, mezclar con huevos y queso, añadir el tocino dorado.",
                fuente = "Cocina Italiana",
                autorId = "uid_de_giovanna",  // Pon aquí el uid correcto si tienes
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
                autorId = "uid_de_ana",  // Pon aquí el uid correcto si tienes
                rating = 3.9f,
                imagenUriString = "https://www.gourmet.cl/wp-content/uploads/2016/09/EnsaladaCesar2.webp"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

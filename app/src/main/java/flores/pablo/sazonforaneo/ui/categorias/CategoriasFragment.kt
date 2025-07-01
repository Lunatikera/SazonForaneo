package flores.pablo.sazonforaneo.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import flores.pablo.sazonforaneo.AgregarNombreDescripcion
import flores.pablo.sazonforaneo.ui.Categoria
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.databinding.FragmentCategoriasBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()
        configurarListenersDeClic()
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

        val adaptadorCategorias = CategoriaAdapter(categorias) { categoria: Categoria ->
            // Navegar usando Safe Args
            val action = CategoriasFragmentDirections
                .actionNavCategoriasToRecetasPorCategoriaFragment(categoria.nombre)

            requireView().findNavController().navigate(action)
        }

        binding.categoriesGridRecyclerview.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adaptadorCategorias
        }
    }


    private fun configurarListenersDeClic() {
        binding.ivPerfil.setOnClickListener {
            Toast.makeText(requireContext(), "Perfil", Toast.LENGTH_SHORT).show()
            val intento = Intent(requireContext(), PerfilConfigActivity::class.java)
            startActivity(intento)
        }

        binding.fabAddRecipe.setOnClickListener {
            val intent = Intent(requireContext(), AgregarNombreDescripcion::class.java)
            startActivity(intent)
        }

        binding.tagsButton.setOnClickListener {
            Toast.makeText(requireContext(), "Botón Etiquetas (funcionalidad pendiente)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package flores.pablo.sazonforaneo.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import flores.pablo.sazonforaneo.AgregarNombreDescripcion
import flores.pablo.sazonforaneo.ui.Categoria
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.databinding.FragmentCategoriasBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment
import java.util.ArrayList
import flores.pablo.sazonforaneo.ui.UsuarioViewModel

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private lateinit var usuarioViewModel: UsuarioViewModel

    private var allRecetas = listOf<Receta>()
    private var selectedTags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioViewModel = ViewModelProvider(requireActivity())[UsuarioViewModel::class.java]

        // Avatar
        usuarioViewModel.imagenPerfilUrl.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.imagen_predeterminada)
                    .circleCrop()
                    .error(R.drawable.imagen_predeterminada)
                    .into(binding.ivPerfil)
            } else {
                binding.ivPerfil.setImageResource(R.drawable.imagen_predeterminada)
            }
        }
        usuarioViewModel.cargarDatosUsuario()

        binding.ivPerfil.setOnClickListener {
            startActivity(Intent(requireContext(), PerfilConfigActivity::class.java))
        }

        binding.tagsButton.setOnClickListener {
            TagsDialogFragment(
                initialTags = selectedTags,
                initialCategories = emptyList()
            ) { tags, _ ->
                selectedTags = tags.toMutableList()
                val bundle = Bundle().apply {
                    putStringArrayList("filtro", ArrayList(selectedTags))
                }
                findNavController().navigate(R.id.nav_explorar, bundle)
            }.show(childFragmentManager, "TagsDialogExplorar")
        }

        configurarRecyclerView()
    }

    private fun configurarRecyclerView() {
        val categorias = listOf(
            Categoria("Entradas", R.drawable.imagen_entradas),
            Categoria("Sopas", R.drawable.imagen_sopas),
            Categoria("Platos Fuertes", R.drawable.imagen_platos_fuertes),
            Categoria("Ensaladas", R.drawable.imagen_ensaladas),
            Categoria("Guarniciones", R.drawable.imagen_guarniciones),
            Categoria("Postres", R.drawable.imagen_postres),
            Categoria("Mariscos", R.drawable.imagen_mariscos),
            Categoria("Desayunos", R.drawable.imagen_desayunos),
            Categoria("Bebidas", R.drawable.imagen_bebidas),
            Categoria("Salsas y Aderezos", R.drawable.imagen_salsas),
            Categoria("Panadería", R.drawable.imagen_panes),
            Categoria("Pastas", R.drawable.imagen_pastas),
            Categoria("Comida Internacional", R.drawable.imagen_internacional),
            Categoria("Vegetariana/Vegana", R.drawable.imagen_vegetarianos),
            Categoria("Rápidas y Fáciles", R.drawable.imagen_facil),
            Categoria("Antojitos Mexicanos", R.drawable.imagen_mexicana)
        )



        val adaptadorCategorias = CategoriaAdapter(categorias) { categoria ->
            val action = CategoriasFragmentDirections
                .actionNavCategoriasToRecetasPorCategoriaFragment(categoria.nombre)
            findNavController().navigate(action)
        }

        binding.categoriesGridRecyclerview.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adaptadorCategorias
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package flores.pablo.sazonforaneo.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.databinding.FragmentCategoriasBinding
import flores.pablo.sazonforaneo.ui.Categoria
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.UsuarioViewModel

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var adaptadorCategorias: CategoriaAdapter
    private lateinit var listaCompleta: List<Categoria>  // ← Guardamos todas las categorías

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

        usuarioViewModel.imagenPerfilUrl.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .load(url)
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

        configurarRecyclerView()
        configurarBusqueda()  // ← Activamos la búsqueda
    }

    private fun configurarRecyclerView() {
        listaCompleta = listOf(
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

        adaptadorCategorias = CategoriaAdapter(listaCompleta) { categoria ->
            val action = CategoriasFragmentDirections
                .actionNavCategoriasToRecetasPorCategoriaFragment(categoria.nombre)
            findNavController().navigate(action)
        }

        binding.categoriesGridRecyclerview.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adaptadorCategorias
        }
    }

    private fun configurarBusqueda() {
        binding.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().lowercase().trim()
                val filtradas = if (texto.isEmpty()) {
                    listaCompleta
                } else {
                    listaCompleta.filter {
                        it.nombre.lowercase().contains(texto)
                    }
                }
                adaptadorCategorias.actualizarLista(filtradas)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

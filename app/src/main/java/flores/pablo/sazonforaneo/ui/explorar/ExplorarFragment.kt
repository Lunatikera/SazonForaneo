package flores.pablo.sazonforaneo.ui.explorar

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import flores.pablo.sazonforaneo.*
import flores.pablo.sazonforaneo.databinding.FragmentExplorarBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogExplorarFragment
import flores.pablo.sazonforaneo.ui.UsuarioViewModel

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExplorarAdapter
    private val recetaRepo = RecetaRepository()
    private val usuarioRepo = UsuarioRepository()
    private val etiquetasRepo = EtiquetasRepository()

    private lateinit var usuarioId: String
    private lateinit var usuarioViewModel: UsuarioViewModel

    private var allRecetas = mutableListOf<Receta>()
    private var selectedTags = mutableListOf<String>()
    private var selectedCategories = mutableListOf<String>()
    private var filtroSeleccionado = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        usuarioViewModel = ViewModelProvider(requireActivity())[UsuarioViewModel::class.java]

        adapter = ExplorarAdapter(emptyList(), usuarioRepo) { receta ->
            val intent = android.content.Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }

        binding.recipesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerview.adapter = adapter

        configurarSpinnerFiltros()
        cargarTodas()

        val nuevaReceta = arguments?.getSerializable("nuevaReceta") as? Receta
        nuevaReceta?.let {
            allRecetas.add(0, it)
            filtrarRecetasPorTags(selectedTags)
            binding.recipesRecyclerview.scrollToPosition(0)
        }


        binding.ivPerfil.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), PerfilConfigActivity::class.java))
        }

        usuarioViewModel.imagenPerfilUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(this)
                .load(url)
                .circleCrop()
                .error(R.drawable.imagen_predeterminada)
                .into(binding.ivPerfil)
        }

        usuarioViewModel.cargarDatosUsuario()

        binding.tagsButton.setOnClickListener {
            etiquetasRepo.obtenerEtiquetas(
                onSuccess = { etiquetasExistentes ->
                    TagsDialogExplorarFragment(
                        initialTags = selectedTags,
                        initialCategories = selectedCategories,
                        initialFiltro = filtroSeleccionado,
                        existingTags = etiquetasExistentes
                    ) { tags, categorias, filtro ->
                        selectedTags = tags.toMutableList()
                        selectedCategories = categorias.toMutableList()
                        filtroSeleccionado = filtro
                        configurarFiltroDesdeDialog(filtroSeleccionado)
                        filtrarRecetasPorTags(selectedTags)
                    }.show(childFragmentManager, "TagsDialogExplorar")
                },
                onFailure = {
                    TagsDialogExplorarFragment(
                        initialTags = selectedTags,
                        initialCategories = selectedCategories,
                        initialFiltro = filtroSeleccionado,
                        existingTags = emptyList()
                    ) { tags, categorias, filtro ->
                        selectedTags = tags.toMutableList()
                        selectedCategories = categorias.toMutableList()
                        filtroSeleccionado = filtro
                        configurarFiltroDesdeDialog(filtroSeleccionado)
                        filtrarRecetasPorTags(selectedTags)
                    }.show(childFragmentManager, "TagsDialogExplorar")
                }
            )
        }
    }

    private fun configurarFiltroDesdeDialog(position: Int) {
        when (position) {
            0 -> cargarTodas()
            1 -> cargarCreadasPorMi()
            2 -> cargarFavoritas()
            3 -> cargarCalificadasPorMi()
        }
    }

    private fun configurarSpinnerFiltros() {
        val opciones = listOf("Todas", "Creadas por mí", "Favoritas", "Calificadas por mí")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // El spinner está en el diálogo, aquí sólo preparas el adaptador
    }

    private fun cargarTodas() {
        recetaRepo.obtenerRecetas(
            onSuccess = { recetas ->
                allRecetas = recetas.toMutableList()
                filtrarRecetasPorTags(selectedTags)
            },
            onFailure = {
                mostrarError(it.message ?: "Error al obtener recetas")
            }
        )
    }

    private fun cargarCreadasPorMi() {
        recetaRepo.obtenerRecetasPorAutor(usuarioId,
            onSuccess = { recetas ->
                allRecetas = recetas.toMutableList()
                filtrarRecetasPorTags(selectedTags)
            },
            onError = { mostrarError(it) }
        )
    }

    private fun cargarFavoritas() {
        recetaRepo.obtenerRecetasFavoritasPor(usuarioId,
            onSuccess = { recetas ->
                allRecetas = recetas.toMutableList()
                filtrarRecetasPorTags(selectedTags)
            },
            onError = { mostrarError(it) }
        )
    }

    private fun cargarCalificadasPorMi() {
        recetaRepo.obtenerRecetasCalificadasPor(usuarioId,
            onSuccess = { recetas ->
                allRecetas = recetas.toMutableList()
                filtrarRecetasPorTags(selectedTags)
            },
            onError = { mostrarError(it) }
        )
    }

    private fun filtrarRecetasPorTags(tags: List<String>) {
        val listaFiltrada = if (tags.isEmpty()) {
            allRecetas
        } else {
            allRecetas.filter { receta -> tags.all { tag -> receta.etiquetas.contains(tag) } }
        }
        adapter.actualizarLista(listaFiltrada)
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

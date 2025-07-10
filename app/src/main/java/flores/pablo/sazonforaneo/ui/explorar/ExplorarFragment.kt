package flores.pablo.sazonforaneo.ui.explorar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.RecetaRepository
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.databinding.FragmentExplorarBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExplorarAdapter
    private val recetaRepo = RecetaRepository()
    private val usuarioRepo = UsuarioRepository()

    private lateinit var usuarioId: String

    private var allRecetas = listOf<Receta>()
    private var selectedTags = mutableListOf<String>()

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

        adapter = ExplorarAdapter(emptyList(), usuarioRepo) { receta ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }

        binding.recipesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerview.adapter = adapter

        configurarSpinnerFiltros()

        cargarTodas()

        val selectedTagsOtherNav = arguments?.getStringArrayList("selectedTags") ?: arrayListOf()
        if (selectedTagsOtherNav != null){

            val tags: MutableList<String> = selectedTagsOtherNav.toMutableList()
            filtrarRecetasPorTags(tags)

        }

        usuarioRepo.obtenerUsuarioActual { usuario ->
            val imagenPerfilUrl = usuario?.imagenPerfil
            if (!imagenPerfilUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imagenPerfilUrl)
                    .placeholder(flores.pablo.sazonforaneo.R.drawable.imagen_predeterminada)
                    .circleCrop()
                    .error(flores.pablo.sazonforaneo.R.drawable.imagen_predeterminada)
                    .into(binding.ivPerfil)
            } else {
                binding.ivPerfil.setImageResource(flores.pablo.sazonforaneo.R.drawable.imagen_predeterminada)
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

    private fun configurarSpinnerFiltros() {
        val opciones = listOf("Todas", "Creadas por mí", "Favoritas", "Calificadas por mí")
        val spinner = binding.spinnerFiltros

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> cargarTodas()
                    1 -> cargarCreadasPorMi()
                    2 -> cargarFavoritas()
                    3 -> cargarCalificadasPorMi()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarTodas() {
        recetaRepo.obtenerRecetas(
            onSuccess = { recetas ->
                allRecetas = recetas
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
                allRecetas = recetas
                filtrarRecetasPorTags(selectedTags)
            },
            onError = { mostrarError(it) }
        )
    }

    private fun cargarFavoritas() {
        recetaRepo.obtenerRecetasFavoritasPor(usuarioId,
            onSuccess = { recetas ->
                allRecetas = recetas
                filtrarRecetasPorTags(selectedTags)
            },
            onError = { mostrarError(it) }
        )
    }

    private fun cargarCalificadasPorMi() {
        recetaRepo.obtenerRecetasCalificadasPor(usuarioId,
            onSuccess = { recetas ->
                allRecetas = recetas
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

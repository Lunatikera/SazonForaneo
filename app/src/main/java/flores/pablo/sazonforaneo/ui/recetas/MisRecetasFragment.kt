package flores.pablo.sazonforaneo.ui.recetas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.RecetaRepository
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.databinding.FragmentMisrecetasBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment
import flores.pablo.sazonforaneo.ui.explorar.ExplorarAdapter
import java.util.ArrayList

class MisRecetasFragment : Fragment() {

    private var _binding: FragmentMisrecetasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MisRecetasAdapter
    private val recetaRepo = RecetaRepository()
    private val usuarioRepo = UsuarioRepository()

    private var allRecetas = listOf<Receta>()
    private var selectedTags = mutableListOf<String>()

    private lateinit var usuarioId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisrecetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (usuarioId.isEmpty()) {
            mostrarError("No se pudo obtener el ID del usuario.")
            return
        }

        Log.d("MisRecetasFragment", "Usuario ID: $usuarioId")

        var where_from = arguments?.getBoolean("where_from")?: true

        adapter = MisRecetasAdapter(where_from, emptyList(), usuarioRepo) { recetaSeleccionada ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", recetaSeleccionada)
            startActivity(intent)
        }

        binding.rvMisRecetas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMisRecetas.adapter = adapter


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
                val bundle = Bundle().apply {
                    putStringArrayList("filtro", ArrayList(selectedTags))
                }
                findNavController().navigate(R.id.nav_explorar, bundle)
            }.show(childFragmentManager, "TagsDialogExplorar")
        }

        if(where_from){

            cargarRecetasCreadasPorMi()

        }else{

            val title: TextView = binding.titleMisRecetas
            title.setText("Recetas Guardadas")

            cargarRecetasGuardadas()

        }

    }

    private fun filtrarRecetasPorTags(tags: List<String>) {
        val listaFiltrada = if (tags.isEmpty()) {
            allRecetas
        } else {
            allRecetas.filter { receta -> tags.all { tag -> receta.etiquetas.contains(tag) } }
        }

    }

    private fun cargarRecetasCreadasPorMi() {
        recetaRepo.obtenerRecetasPorAutor(usuarioId,
            onSuccess = { recetas ->
                Log.d("MisRecetasFragment", "Recetas encontradas: ${recetas.size}")
                recetas.forEach {
                    Log.d("MisRecetasFragment", "Receta: ${it.nombre}")
                }
                actualizarLista(recetas)
            },
            onError = { errorMsg ->
                mostrarError(errorMsg)
            }
        )
    }

    private fun cargarRecetasGuardadas(){

        recetaRepo.obtenerRecetasFavoritasPor(usuarioId,

            onSuccess = {

                recetas ->
                Log.d("MisRecetasFragment", "Recetas encontradas: ${recetas.size}")
                recetas.forEach{

                    Log.d("MisRecetasFragment", "Receta: ${it.nombre}")

                }
                actualizarLista(recetas)

            },
            onError = {

                errorMsg ->
                mostrarError(errorMsg)

            }
            )

    }

    private fun actualizarLista(recetas: List<Receta>) {
        adapter.recetas = recetas
        adapter.notifyDataSetChanged()
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
        Log.e("MisRecetasFragment", mensaje)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package flores.pablo.sazonforaneo.ui.recetas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import flores.pablo.sazonforaneo.*
import flores.pablo.sazonforaneo.databinding.FragmentMisrecetasBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment
import flores.pablo.sazonforaneo.ui.UsuarioViewModel
import java.util.ArrayList

class MisRecetasFragment : Fragment() {

    private var _binding: FragmentMisrecetasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MisRecetasAdapter
    private val usuarioRepo = UsuarioRepository()
    private val etiquetasRepo = EtiquetasRepository()

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var recetaViewModel: RecetaViewModel
    private lateinit var usuarioId: String

    private var selectedTags = mutableListOf<String>()
    private var selectedCategories = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisrecetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioViewModel = ViewModelProvider(requireActivity())[UsuarioViewModel::class.java]
        recetaViewModel = ViewModelProvider(this)[RecetaViewModel::class.java]

        usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (usuarioId.isEmpty()) {
            mostrarError("No se pudo obtener el ID del usuario.")
            return
        }

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

        binding.tagsButton.setOnClickListener {
            etiquetasRepo.obtenerEtiquetas(
                onSuccess = { etiquetasExistentes ->
                    TagsDialogFragment(
                        initialTags = selectedTags,
                        initialCategories = selectedCategories,
                        existingTags = etiquetasExistentes
                    ) { tags, categorias ->
                        selectedTags = tags.toMutableList()
                        selectedCategories = categorias.toMutableList()
                        aplicarFiltrosEnMisRecetas()
                    }.show(childFragmentManager, "TagsDialogMisRecetas")
                },
                onFailure = {
                    TagsDialogFragment(
                        initialTags = selectedTags,
                        initialCategories = selectedCategories,
                        existingTags = emptyList()
                    ) { tags, categorias ->
                        selectedTags = tags.toMutableList()
                        selectedCategories = categorias.toMutableList()
                        aplicarFiltrosEnMisRecetas()
                    }.show(childFragmentManager, "TagsDialogMisRecetas")
                }
            )
        }



        val whereFrom = arguments?.getBoolean("where_from") ?: true

        adapter = MisRecetasAdapter(
            where_from = whereFrom,
            recetas = emptyList(),
            usuarioRepo = usuarioRepo,
            onItemClick = { receta ->
                val intent = Intent(requireContext(), DetalleReceta::class.java)
                intent.putExtra("receta", receta)
                startActivity(intent)
            },
            onEliminarClick = { receta ->
                mostrarDialogoConfirmacionEliminar(receta)
            },
            onEditarClick = { receta ->
                editarReceta(receta)
            },
            onCambiarVisibilidadClick = { receta ->
                cambiarVisibilidad(receta)
            }
        )

        binding.rvMisRecetas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMisRecetas.adapter = adapter

        recetaViewModel.recetas.observe(viewLifecycleOwner) { recetas ->
            adapter.recetas = recetas
            adapter.notifyDataSetChanged()
        }

        recetaViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { mostrarError(it.message ?: "Error desconocido") }
        }

        recetaViewModel.guardadoExitoso.observe(viewLifecycleOwner) { exito ->
            if (exito) {
                Toast.makeText(requireContext(), "Operación exitosa", Toast.LENGTH_SHORT).show()
                cargarRecetasCreadasPorMi()
            }
        }

        if (whereFrom) {
            cargarRecetasCreadasPorMi()
        } else {
            binding.titleMisRecetas.text = "Recetas Guardadas"
            cargarRecetasGuardadas()
        }
    }

    private fun eliminarReceta(receta: Receta) {
        recetaViewModel.eliminarReceta(receta)
    }

    private fun editarReceta(receta: Receta) {
        Toast.makeText(requireContext(), "Editar receta: ${receta.nombre}", Toast.LENGTH_SHORT).show()
    }

    private fun cambiarVisibilidad(receta: Receta) {
        receta.visibilidad = if (receta.visibilidad == "publico") "privado" else "publico"
        recetaViewModel.actualizarReceta(receta)
        adapter.notifyDataSetChanged()
    }

    private fun cargarRecetasCreadasPorMi() {
        recetaViewModel.cargarRecetasPorAutor(usuarioId)
    }

    private fun cargarRecetasGuardadas() {
        recetaViewModel.cargarRecetasFavoritasPor(usuarioId)
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
        Log.e("MisRecetasFragment", mensaje)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun aplicarFiltrosEnMisRecetas() {
        val recetasOriginales = recetaViewModel.recetas.value ?: return

        if (selectedTags.isEmpty() && selectedCategories.isEmpty()) {
            adapter.recetas = recetasOriginales
            adapter.notifyDataSetChanged()
            return
        }

        val filtradas = recetasOriginales.filter { receta ->
            val coincideTag = selectedTags.isEmpty() || selectedTags.any { it in receta.etiquetas }
            val coincideCategoria = selectedCategories.isEmpty() || selectedCategories.any { it in receta.categorias }

            if (selectedTags.isNotEmpty() && selectedCategories.isNotEmpty()) {
                coincideTag && coincideCategoria
            } else {
                (selectedTags.isNotEmpty() && coincideTag) || (selectedCategories.isNotEmpty() && coincideCategoria)
            }
        }

        adapter.recetas = filtradas
        adapter.notifyDataSetChanged()
    }


    private fun mostrarDialogoConfirmacionEliminar(receta: Receta) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar la receta \"${receta.nombre}\"?")
        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarReceta(receta)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        cargarRecetasCreadasPorMi()
    }
}

package flores.pablo.sazonforaneo.ui.recetas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.databinding.FragmentMisrecetasBinding
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment
import flores.pablo.sazonforaneo.ui.UsuarioViewModel
import flores.pablo.sazonforaneo.RecetaViewModel
import java.util.ArrayList

class MisRecetasFragment : Fragment() {

    private var _binding: FragmentMisrecetasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MisRecetasAdapter
    private val usuarioRepo = UsuarioRepository()

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var recetaViewModel: RecetaViewModel
    private lateinit var usuarioId: String

    private var selectedTags = mutableListOf<String>()

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
        // TODO: Navegar a la pantalla de edición con la receta
    }

    private fun cambiarVisibilidad(receta: Receta) {
        // Cambiar visibilidad localmente (toggle)
        receta.visibilidad = if (receta.visibilidad == "publico") "privado" else "publico"

        // Actualizar en Firestore vía ViewModel
        recetaViewModel.actualizarReceta(receta)

        // Opcional: para que se vea cambio inmediato
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

}

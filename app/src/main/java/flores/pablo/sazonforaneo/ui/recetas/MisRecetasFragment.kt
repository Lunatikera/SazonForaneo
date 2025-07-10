package flores.pablo.sazonforaneo.ui.recetas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.RecetaRepository
import flores.pablo.sazonforaneo.UsuarioRepository
import flores.pablo.sazonforaneo.databinding.FragmentMisrecetasBinding

class MisRecetasFragment : Fragment() {

    private var _binding: FragmentMisrecetasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MisRecetasAdapter
    private val recetaRepo = RecetaRepository()
    private val usuarioRepo = UsuarioRepository()

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

        adapter = MisRecetasAdapter(emptyList(), usuarioRepo) { recetaSeleccionada ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", recetaSeleccionada)
            startActivity(intent)
        }

        binding.rvMisRecetas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMisRecetas.adapter = adapter

        cargarRecetasCreadasPorMi()
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

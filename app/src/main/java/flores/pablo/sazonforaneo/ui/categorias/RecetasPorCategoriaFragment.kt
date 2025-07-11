package flores.pablo.sazonforaneo.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment
import flores.pablo.sazonforaneo.ui.UsuarioViewModel
import flores.pablo.sazonforaneo.ui.explorar.ExplorarAdapter
import flores.pablo.sazonforaneo.RecetaViewModel
import flores.pablo.sazonforaneo.UsuarioRepository

class RecetasPorCategoriaFragment : Fragment() {

    private lateinit var tvTituloCategoria: TextView
    private lateinit var etBuscar: EditText
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var adapter: ExplorarAdapter
    private lateinit var ivPerfil: ImageView
    private lateinit var tagsButton: Button

    private val args: RecetasPorCategoriaFragmentArgs by navArgs()
    private val usuarioRepo = UsuarioRepository()

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var recetaViewModel: RecetaViewModel

    private var allRecetasForCategory = listOf<Receta>()
    private var selectedTags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_recetas_por_categoria, container, false)

        tvTituloCategoria = view.findViewById(R.id.tvTituloCategoria)
        etBuscar = view.findViewById(R.id.etBuscar)
        recyclerView = view.findViewById(R.id.recyclerview_recetas_categoria)
        ivPerfil = view.findViewById(R.id.ivPerfil)
        tagsButton = view.findViewById(R.id.tags_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioViewModel = ViewModelProvider(requireActivity())[UsuarioViewModel::class.java]
        recetaViewModel = ViewModelProvider(requireActivity())[RecetaViewModel::class.java]

        val nombreCategoria = args.categoriaNombre
        tvTituloCategoria.text = "Recetas de $nombreCategoria"

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ExplorarAdapter(emptyList(), usuarioRepo) { receta ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", receta)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        recetaViewModel.recetas.observe(viewLifecycleOwner) { recetas ->
            allRecetasForCategory = recetas
            filtrarRecetasPorTagsYBusqueda(selectedTags, etBuscar.text.toString())
        }

        recetaViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error al cargar recetas: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        // 🔄 ¡Usamos el nuevo método específico por categoría!
        recetaViewModel.cargarRecetasPorCategoria(nombreCategoria)

        usuarioViewModel.imagenPerfilUrl.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .load(url)
                    .circleCrop()
                    .error(R.drawable.imagen_predeterminada)
                    .into(ivPerfil)
            } else {
                ivPerfil.setImageResource(R.drawable.imagen_predeterminada)
            }
        }
        usuarioViewModel.cargarDatosUsuario()

        ivPerfil.setOnClickListener {
            startActivity(Intent(requireContext(), PerfilConfigActivity::class.java))
        }

        tagsButton.setOnClickListener {
            val dialog = TagsDialogFragment(
                initialTags = selectedTags,
                initialCategories = emptyList()
            ) { tags, _ ->
                selectedTags = tags.toMutableList()
                filtrarRecetasPorTagsYBusqueda(tags, etBuscar.text.toString())
            }
            dialog.show(childFragmentManager, "TagsDialogRecetasCategoria")
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarRecetasPorTagsYBusqueda(selectedTags, s.toString())
            }
        })
    }

    private fun filtrarRecetasPorTagsYBusqueda(tags: List<String>, textoBusqueda: String) {
        val texto = textoBusqueda.lowercase().trim()

        var filtradas = if (tags.isEmpty()) allRecetasForCategory
        else allRecetasForCategory.filter { receta ->
            tags.all { tag -> receta.etiquetas.contains(tag) }
        }

        if (texto.isNotEmpty()) {
            filtradas = filtradas.filter {
                it.nombre.lowercase().contains(texto) || it.descripcion.lowercase().contains(texto)
            }
        }

        adapter.actualizarLista(filtradas)
    }

    override fun onResume() {
        super.onResume()
        recetaViewModel.cargarRecetasPorCategoria(args.categoriaNombre)
    }
}

package flores.pablo.sazonforaneo.ui.misrecetas

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flores.pablo.sazonforaneo.DetalleReceta
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.ui.PerfilConfigActivity
import flores.pablo.sazonforaneo.ui.TagsDialogFragment
import flores.pablo.sazonforaneo.ui.explorar.ExplorarAdapter

class MisRecetasFragment : Fragment() {

    private lateinit var ivPerfil: ImageView
    private lateinit var etBuscar: EditText
    private lateinit var tagsButton: Button
    private lateinit var tvTituloMisRecetas: TextView
    private lateinit var rvMisRecetas: RecyclerView

    private lateinit var adapter: ExplorarAdapter
    private var allMisRecetas = listOf<Receta>()
    private var selectedTags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_misrecetas, container, false)

        ivPerfil = view.findViewById(R.id.ivPerfil)
        etBuscar = view.findViewById(R.id.etBuscar)
        tagsButton = view.findViewById(R.id.tags_button)
        tvTituloMisRecetas = view.findViewById(R.id.titleMisRecetas)
        rvMisRecetas = view.findViewById(R.id.rvMisRecetas)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allMisRecetas = obtenerRecetasEjemploUsuario()
        adapter = ExplorarAdapter(allMisRecetas) { recetaSeleccionada ->
            val intent = Intent(requireContext(), DetalleReceta::class.java)
            intent.putExtra("receta", recetaSeleccionada)
            startActivity(intent)
        }

        rvMisRecetas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MisRecetasFragment.adapter
        }

        setupListeners()
    }

    private fun setupListeners() {
        ivPerfil.setOnClickListener {
            val intent = Intent(requireContext(), PerfilConfigActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "Perfil", Toast.LENGTH_SHORT).show()
        }

        tagsButton.setOnClickListener {
            val dialog = TagsDialogFragment(selectedTags) { tags ->
                selectedTags = tags.toMutableList()
                applyFilters()
            }
            dialog.show(childFragmentManager, "TagsDialogMisRecetas")
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun applyFilters() {
        var filteredList = allMisRecetas

        val searchText = etBuscar.text.toString().trim().lowercase()
        if (searchText.isNotEmpty()) {
            filteredList = filteredList.filter { receta ->
                receta.nombre.lowercase().contains(searchText) ||
                        receta.descripcion.lowercase().contains(searchText)
            }
        }

        if (selectedTags.isNotEmpty()) {
            filteredList = filteredList.filter { receta ->
                selectedTags.all { tag -> receta.etiquetas.contains(tag) }
            }
        }

        adapter.actualizarLista(filteredList)
    }

    private fun obtenerRecetasEjemploUsuario(): List<Receta> {
        return listOf(
            Receta(
                nombre = "Brownies de Chocolate",
                descripcion = "Deliciosos brownies caseros con trozos de chocolate.",
                categorias = listOf("Postres"),
                etiquetas = listOf("Chocolate", "Horneado", "Postre"),
                visibilidad = "Pública",
                ingredientes = listOf("Harina", "Azúcar", "Cacao", "Huevos", "Mantequilla", "Chocolate semi-amargo"),
                instrucciones = "Mezclar ingredientes secos, añadir húmedos, hornear a 180°C por 25 minutos.",
                fuente = "Receta familiar",
                autor = "Tú",
                rating = 5.0f,
                imagenUriString = "https://example.com/brownies.jpg"
            ),
            Receta(
                nombre = "Pechugas Rellenas de Espinacas",
                descripcion = "Pechugas de pollo rellenas con espinacas y queso, al horno.",
                categorias = listOf("Platos Fuertes", "Saludable"),
                etiquetas = listOf("Pollo", "Espinacas", "Cena", "Saludable"),
                visibilidad = "Privada",
                ingredientes = listOf("Pechuga de pollo", "Espinacas", "Queso crema", "Ajo", "Cebolla", "Especias"),
                instrucciones = "Rellenar pechugas, sellar y hornear hasta cocción.",
                fuente = "Mi propia creación",
                autor = "Tú",
                rating = 4.5f,
                imagenUriString = "https://example.com/pechugas.jpg"
            ),
            Receta(
                nombre = "Sopa de Lentejas",
                descripcion = "Sopa nutritiva de lentejas con verduras variadas.",
                categorias = listOf("Sopas", "Saludable", "Vegetariana"),
                etiquetas = listOf("Lentejas", "Sopa", "Vegetariana", "Saludable"),
                visibilidad = "Pública",
                ingredientes = listOf("Lentejas", "Zanahoria", "Papa", "Cebolla", "Apio", "Caldo de verduras"),
                instrucciones = "Cocer lentejas con verduras hasta que estén tiernas. Sazonar al gusto.",
                fuente = "Recetario de la abuela",
                autor = "Tú",
                rating = 4.2f,
                imagenUriString = "https://example.com/lentejas.jpg"
            )
        )
    }


}
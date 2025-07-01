package flores.pablo.sazonforaneo.ui.categorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.Receta
import flores.pablo.sazonforaneo.ui.explorar.ExplorarAdapter

class RecetasPorCategoriaFragment : Fragment() {

    private lateinit var tvTituloCategoria: TextView
    private lateinit var etBuscar: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExplorarAdapter

    private var nombreCategoria: String? = null
    private var recetasFiltradas: List<Receta> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nombreCategoria = it.getString("categoria")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_recetas_por_categoria, container, false)

        tvTituloCategoria = view.findViewById(R.id.tvTituloCategoria)
        etBuscar = view.findViewById(R.id.etBuscar)
        recyclerView = view.findViewById(R.id.recyclerview_recetas_categoria)

        tvTituloCategoria.text = "Recetas de ${nombreCategoria ?: "Categoría"}"

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val recetas = getMockRecetas()

        recetasFiltradas = recetas.filter { it.categorias.contains(nombreCategoria) }

        adapter = ExplorarAdapter(recetasFiltradas) { receta ->
        }

        recyclerView.adapter = adapter

        return view
    }

    private fun getMockRecetas(): List<Receta> {
        return listOf(
            Receta(
                nombre = "Tacos al pastor",
                autor = "Chef Juan",
                imagenUriString = null,
                rating = 4.5f,
                categorias = listOf("Mexicana"),
                etiquetas = listOf("Tacos", "Cena")
            ),
            Receta(
                nombre = "Pizza Margarita",
                autor = "Chef Luisa",
                imagenUriString = null,
                rating = 4.0f,
                categorias = listOf("Italiana"),
                etiquetas = listOf("Pizza", "Queso")
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(categoria: String) =
            RecetasPorCategoriaFragment().apply {
                arguments = Bundle().apply {
                    putString("categoria", categoria)
                }
            }
    }
}

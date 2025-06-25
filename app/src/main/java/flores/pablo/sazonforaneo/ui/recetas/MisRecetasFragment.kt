package flores.pablo.sazonforaneo.ui.recetas

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Grid
import androidx.core.graphics.drawable.toIcon
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import flores.pablo.sazonforaneo.R
import flores.pablo.sazonforaneo.databinding.FragmentMisrecetasBinding
import flores.pablo.sazonforaneo.ui.Receta
import flores.pablo.sazonforaneo.ui.Usuario

class MisRecetasFragment : Fragment() {


    private var adaptador: AdapaterReceta? = null
    private var _binding: FragmentMisrecetasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val misRecetasViewModel =
            ViewModelProvider(this).get(MisRecetasViewModel::class.java)

        _binding = FragmentMisrecetasBinding.inflate(inflater, container, false)

        val root: View = binding.root


        var lista = crearRecetas()

        adaptador = AdapaterReceta(root.context, lista)

        val gridView: GridView = root.findViewById(R.id.gvRecetas)
        gridView.adapter = adaptador



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun crearRecetas(): ArrayList<Receta>{

        val imagenUri:  Uri = Uri.parse("android.resource://${requireContext().packageName}/${R.drawable.pizza_italiana}")
        var usuario = Usuario("Amos", "Amos@gmail.com", "6332526372", "20 de julio")
        var etiquetas = ArrayList<String>()
        etiquetas.add("pizza")
        etiquetas.add("buena")
        etiquetas.add("bonita")
        etiquetas.add("rica")
        etiquetas.add("ola")
        var lista = ArrayList<Receta>()
        lista.add(Receta("Pizza Italiana", "Pizza muy buena", listOf("Pizza"),  etiquetas, "publica", listOf("peperoni", "queso", "harina", "tomate"), "primero la haces y luego te la comes", "", imagenUri, usuario))


        lista.add(Receta("Pizza Italiana", "Pizza muy buena", listOf("Pizza"), etiquetas, "publica", listOf("peperoni", "queso", "harina", "tomate"), "primero la haces y luego te la comes", "", imagenUri, usuario))
        lista.add(Receta("Pizza Italiana", "Pizza muy buena", listOf("Pizza"), etiquetas, "publica", listOf("peperoni", "queso", "harina", "tomate"), "primero la haces y luego te la comes", "", imagenUri, usuario))
        lista.add(Receta("Pizza Italiana", "Pizza muy buena", listOf("Pizza"), etiquetas, "publica", listOf("peperoni", "queso", "harina", "tomate"), "primero la haces y luego te la comes", "", imagenUri, usuario))
        lista.add(Receta("Pizza Italiana", "Pizza muy buena", listOf("Pizza"), etiquetas, "publica", listOf("peperoni", "queso", "harina", "tomate"), "primero la haces y luego te la comes", "", imagenUri, usuario))

        return lista

    }

    private class AdapterEtiqueta: BaseAdapter {


        var contexto: Context? = null
        var etiquetas: ArrayList<String>

        constructor(contexto: Context?, etiquetas: ArrayList<String>) {

            this.contexto = contexto
            this.etiquetas = etiquetas

        }

        override fun getCount(): Int {
            return etiquetas.size
        }

        override fun getItem(position: Int): Any {
            return etiquetas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var etiqueta = etiquetas[position]
            var inflador = LayoutInflater.from(contexto)

            val view = inflador.inflate(R.layout.etiqueta_element, null)

            val nombreEtiqueta : TextView = view.findViewById(R.id.nombreEtiqueta)

            nombreEtiqueta.setText(etiqueta)

            return view

        }


    }

    private class AdapaterReceta: BaseAdapter{

        var contexto: Context? = null
        var recetas = ArrayList<Receta>()
        private var adaptadorEtiqueta: AdapterEtiqueta? = null

        constructor(contexto: Context, recetas: ArrayList<Receta>){

            this.recetas = recetas
            this.contexto = contexto

        }

        override fun getCount(): Int {
            return recetas.size
        }

        override fun getItem(position: Int): Any {
            return recetas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var receta = recetas[position]
            var inflador = LayoutInflater.from(contexto)

            val vista = inflador.inflate(R.layout.receta_element, null)

            val nombreReceta: TextView = vista.findViewById(R.id.tvTituloReceta)
            val autor: TextView = vista.findViewById(R.id.tvAutor)
            val foto: ImageView = vista.findViewById(R.id.ivFoto)
            val gridViewEtiquetas: GridView = vista.findViewById(R.id.gvEtiquetas)


            adaptadorEtiqueta = AdapterEtiqueta(contexto, receta.etiquetas)
            gridViewEtiquetas.adapter = adaptadorEtiqueta





            nombreReceta.setText(receta.nombre)
            autor.setText(receta.owner.nombre)
            foto.setImageURI(receta.imagenUri)

            return vista

        }

    }

}
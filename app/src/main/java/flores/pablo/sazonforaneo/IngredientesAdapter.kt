package flores.pablo.sazonforaneo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class IngredientesAdapter(
    private val context: Context,
    private val ingredientes: MutableList<String>
) : BaseAdapter() {

    override fun getCount(): Int = ingredientes.size
    override fun getItem(position: Int): Any = ingredientes[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_ingrediente, parent, false)

        val tvIngrediente = view.findViewById<TextView>(R.id.tvIngrediente)
        val btnEliminar = view.findViewById<ImageView>(R.id.btnEliminar)

        tvIngrediente.text = ingredientes[position]

        btnEliminar.setOnClickListener {
            ingredientes.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }
}

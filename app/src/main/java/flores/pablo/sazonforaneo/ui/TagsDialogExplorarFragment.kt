package flores.pablo.sazonforaneo.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.flexbox.FlexboxLayout
import flores.pablo.sazonforaneo.R

class TagsDialogExplorarFragment(
    private val initialTags: List<String> = emptyList(),
    private val initialCategories: List<String> = emptyList(),
    private val initialFiltro: Int = 0,
    private val existingTags: List<String> = emptyList(), // <-- Etiquetas para autocomplete
    private val onApply: (tags: List<String>, categories: List<String>, filtro: Int) -> Unit
) : DialogFragment() {

    private lateinit var etNewTag: AutoCompleteTextView
    private lateinit var btnAddTag: Button
    private lateinit var flexTagsContainer: FlexboxLayout
    private lateinit var flexCategoriesContainer: FlexboxLayout
    private lateinit var btnApplyTags: Button
    private lateinit var spinnerFiltro: Spinner

    private val currentTags = mutableListOf<String>()
    private val selectedCategories = mutableListOf<String>()

    private val allCategories = listOf(
        "Entradas",
        "Sopas",
        "Platos Fuertes",
        "Ensaladas",
        "Guarniciones",
        "Postres",
        "Mariscos",
        "Desayunos",
        "Bebidas",
        "Salsas y Aderezos",
        "Panadería",
        "Pastas",
        "Comida Internacional",
        "Vegetariana/Vegana",
        "Rápidas y Fáciles",
        "Antojitos Mexicanos"
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_tags_explorar, container, false)

        // Referencias a vistas
        etNewTag = view.findViewById(R.id.etNewTag)
        btnAddTag = view.findViewById(R.id.btnAddTag)
        flexTagsContainer = view.findViewById(R.id.flexTagsContainer)
        flexCategoriesContainer = view.findViewById(R.id.flexCategoriesContainer)
        btnApplyTags = view.findViewById(R.id.btnApplyTags)
        spinnerFiltro = view.findViewById(R.id.spinnerFiltroOrigen)

        currentTags.addAll(initialTags)
        selectedCategories.addAll(initialCategories)

        // Configuramos autocomplete para el AutoCompleteTextView
        val adapterAutoComplete = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, existingTags)
        etNewTag.setAdapter(adapterAutoComplete)
        etNewTag.threshold = 1 // Comienza a sugerir tras 1 carácter

        configurarSpinnerFiltro()
        cargarCategorias()
        updateTagViews()

        btnAddTag.setOnClickListener {
            val newTag = etNewTag.text.toString().trim()
            if (newTag.isNotEmpty() && !currentTags.contains(newTag)) {
                currentTags.add(newTag)
                etNewTag.text.clear()
                updateTagViews()
            }
        }

        btnApplyTags.setOnClickListener {
            val filtroSeleccionado = spinnerFiltro.selectedItemPosition
            onApply(currentTags.toList(), selectedCategories.toList(), filtroSeleccionado)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.95).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun configurarSpinnerFiltro() {
        val opciones = listOf("Todas", "Creadas por mí", "Favoritas", "Calificadas por mí")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltro.adapter = spinnerAdapter
        spinnerFiltro.setSelection(initialFiltro)
    }

    private fun cargarCategorias() {
        flexCategoriesContainer.removeAllViews()

        for (categoria in allCategories) {
            val chip = TextView(requireContext()).apply {
                text = categoria
                setPadding(32, 16, 32, 16)
                setTextColor(Color.parseColor("#44291D"))
                setBackgroundResource(R.drawable.chip_categoria)
                textSize = 14f
                setOnClickListener {
                    toggleCategoria(categoria, this)
                }
            }

            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 8, 8, 8)
            chip.layoutParams = params

            if (selectedCategories.contains(categoria)) {
                chip.setBackgroundResource(R.drawable.chip_categoria_selected)
                chip.setTextColor(Color.WHITE)
            }

            flexCategoriesContainer.addView(chip)
        }
    }

    private fun toggleCategoria(categoria: String, view: TextView) {
        if (selectedCategories.contains(categoria)) {
            selectedCategories.remove(categoria)
            view.setBackgroundResource(R.drawable.chip_categoria)
            view.setTextColor(Color.parseColor("#44291D"))
        } else {
            selectedCategories.add(categoria)
            view.setBackgroundResource(R.drawable.chip_categoria_selected)
            view.setTextColor(Color.WHITE)
        }
    }

    private fun updateTagViews() {
        flexTagsContainer.removeAllViews()
        currentTags.forEach { tag ->
            val chip = TextView(requireContext()).apply {
                text = tag
                setPadding(24, 12, 24, 12)
                setTextColor(Color.WHITE)
                setBackgroundResource(R.drawable.tag_green_background)
                layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 16, 16)
                }

                setOnClickListener {
                    currentTags.remove(tag)
                    updateTagViews()
                }
            }
            flexTagsContainer.addView(chip)
        }
    }
}

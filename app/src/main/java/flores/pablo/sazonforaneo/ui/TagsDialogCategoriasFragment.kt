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

class TagsDialogCategoriasFragment(
    private val initialTags: List<String> = emptyList(),
    private val existingTags: List<String> = emptyList(),
    private val onApply: (tags: List<String>) -> Unit
) : DialogFragment() {

    private lateinit var etNewTag: AutoCompleteTextView
    private lateinit var btnAddTag: Button
    private lateinit var flexTagsContainer: FlexboxLayout
    private lateinit var btnApplyTags: Button

    private val currentTags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_tags_categorias, container, false)

        etNewTag = view.findViewById(R.id.etNewTag)
        btnAddTag = view.findViewById(R.id.btnAddTag)
        flexTagsContainer = view.findViewById(R.id.flexTagsContainer)
        btnApplyTags = view.findViewById(R.id.btnApplyTags)

        currentTags.addAll(initialTags)

        // Setup autocomplete
        val adapterAutoComplete = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, existingTags)
        etNewTag.setAdapter(adapterAutoComplete)
        etNewTag.threshold = 1

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
            onApply(currentTags.toList())
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

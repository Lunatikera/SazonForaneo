package flores.pablo.sazonforaneo.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.flexbox.FlexboxLayout
import flores.pablo.sazonforaneo.R

class TagsDialogFragment(
    private val initialTags: List<String>,
    private val onTagsApplied: (List<String>) -> Unit
) : DialogFragment() {

    private lateinit var etNewTag: EditText
    private lateinit var btnAddTag: Button
    private lateinit var flexTagsContainer: FlexboxLayout
    private lateinit var btnApplyTags: Button

    private val currentTags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_tags, container, false)

        etNewTag = view.findViewById(R.id.etNewTag)
        btnAddTag = view.findViewById(R.id.btnAddTag)
        flexTagsContainer = view.findViewById(R.id.flexTagsContainer)
        btnApplyTags = view.findViewById(R.id.btnApplyTags)

        currentTags.addAll(initialTags)
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
            onTagsApplied(currentTags)
            dismiss()
        }

        return view
    }

    private fun updateTagViews() {
        flexTagsContainer.removeAllViews()
        for (tag in currentTags) {
            val tagView = createTagView(tag)
            flexTagsContainer.addView(tagView)
        }
    }

    private fun createTagView(tag: String): View {
        val tagView = TextView(requireContext()).apply {
            text = tag
            setPadding(24, 12, 24, 12)
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.tag_green_background)
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 16, 16)
            layoutParams = params
        }

        tagView.setOnClickListener {
            currentTags.remove(tag)
            updateTagViews()
        }

        return tagView
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

}

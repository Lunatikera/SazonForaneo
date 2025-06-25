package flores.pablo.sazonforaneo.ui.recetas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import flores.pablo.sazonforaneo.databinding.FragmentMisrecetasBinding

class MisRecetasFragment : Fragment() {

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

        val textView: TextView = binding.textNotifications
        misRecetasViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
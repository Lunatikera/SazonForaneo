package flores.pablo.sazonforaneo.ui.explorar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import flores.pablo.sazonforaneo.databinding.FragmentExplorarBinding

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val explorarViewModel =
            ViewModelProvider(this).get(ExplorarViewModel::class.java)

        _binding = FragmentExplorarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        explorarViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
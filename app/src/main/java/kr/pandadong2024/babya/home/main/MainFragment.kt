package kr.pandadong2024.babya.home.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import kr.pandadong2024.babya.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val txtHello = binding.titleText
        val text = "Hello World!"
//        val purple = ContextCompat.getColor(requireContext(), R.color.yellow)
//        val teal = ContextCompat.getColor(requireContext(), R.color.blue)
        val spannable = text.toSpannable()
//        spannable[0..text.length] = LinearGradient(0.0f,0.0f,0.0f,0.0f, teal, purple, Shader.TileMode.CLAMP)
        txtHello.text = spannable
        return binding.root
    }
}
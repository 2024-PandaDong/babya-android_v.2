package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup2Binding
import kr.pandadong2024.babya.databinding.FragmentSignup3Binding
import kr.pandadong2024.babya.databinding.FragmentSignup4Binding


class Signup4 : Fragment() {

    private var _binding: FragmentSignup4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup4Binding.inflate(inflater, container, false)


        return binding.root
    }
}
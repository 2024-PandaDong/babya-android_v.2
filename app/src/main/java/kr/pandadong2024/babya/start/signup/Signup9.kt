package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup8Binding
import kr.pandadong2024.babya.databinding.FragmentSignup9Binding

class Signup9 : Fragment() {

    private var _binding: FragmentSignup9Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup9Binding.inflate(inflater, container, false)


        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup9_to_signup5)
        }

        return binding.root
    }

}
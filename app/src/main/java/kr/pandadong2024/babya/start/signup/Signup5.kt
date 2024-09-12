package kr.pandadong2024.babya.start.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup2Binding
import kr.pandadong2024.babya.databinding.FragmentSignup3Binding
import kr.pandadong2024.babya.databinding.FragmentSignup5Binding


class Signup5 : Fragment() {

    private var _binding: FragmentSignup5Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup5Binding.inflate(inflater, container, false)


        binding.signup1Button.setOnClickListener {
            findNavController().navigate(R.id.action_signup5_to_signup9)
        }

        binding.signup2Button.setOnClickListener {
            findNavController().navigate(R.id.action_signup5_to_signup6)
        }

        binding.signup3Button.setOnClickListener {
            findNavController().navigate(R.id.action_signup5_to_signup7)
        }

        binding.signup4Button.setOnClickListener {
            findNavController().navigate(R.id.action_signup5_to_signup8)
        }

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_signup5_to_signup3)
        }



        return binding.root
    }

}
package kr.pandadong2024.babya.start.signup

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentSignup1Binding
import kr.pandadong2024.babya.databinding.FragmentSignup2Binding

class Signup1 : Fragment() {

    private var _binding: FragmentSignup1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignup1Binding.inflate(inflater, container, false)

        binding.nextBtn.isEnabled = true

        binding.nextBtn.setOnClickListener {
            Log.d(TAG, "onCreateView: dddd")
//            findNavController().navigate(R.id.action_signup1_to_signup2)
            findNavController().navigate(R.id.action_signup1_to_signup5)
        }


        return binding.root
    }
}
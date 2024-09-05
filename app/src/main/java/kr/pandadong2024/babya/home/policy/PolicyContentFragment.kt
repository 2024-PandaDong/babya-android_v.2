package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyContentBinding

class PolicyContentFragment : Fragment() {
    var _binding: FragmentPolicyContentBinding? = null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyContentBinding.inflate(inflater, container, false)

        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_policyContentFragment_to_policyMainFragment)
        }
        return binding.root
    }
}
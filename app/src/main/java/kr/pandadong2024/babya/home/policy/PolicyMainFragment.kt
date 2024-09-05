package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.pandadong2024.babya.databinding.FragmentPolicyMainBinding

class PolicyMainFragment : Fragment() {

    var _binding : FragmentPolicyMainBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyMainBinding.inflate(inflater, container, false)
        return binding.root
    }
}
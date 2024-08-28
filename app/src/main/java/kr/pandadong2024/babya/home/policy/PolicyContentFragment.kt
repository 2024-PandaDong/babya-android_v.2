package kr.pandadong2024.babya.home.policy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyContentBinding

/**
 * A simple [Fragment] subclass.
 * Use the [PolicyContentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PolicyContentFragment : Fragment() {
    var _binding: FragmentPolicyContentBinding? = null
    private val binding = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPolicyContentBinding.inflate(inflater, container, false)

        return inflater.inflate(R.layout.fragment_policy_content, container, false)
    }
}
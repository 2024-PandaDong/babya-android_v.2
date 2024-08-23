package kr.pandadong2024.babya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.pandadong2024.babya.databinding.FragmentPolicyContentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
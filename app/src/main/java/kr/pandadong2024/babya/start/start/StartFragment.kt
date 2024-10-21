package kr.pandadong2024.babya.start.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.HomeActivity
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentStartBinding
import kr.pandadong2024.babya.server.local.BabyaDB

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater, container, false)


        lifecycleScope.launch(Dispatchers.IO) {
            val accessToken =
                BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken
            delay(1500)
            withContext(Dispatchers.Main) {
                Log.d("StartFragment", "accessToken : $accessToken")
                if (!accessToken.isNullOrEmpty()) {
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                    requireActivity().finish()
                } else {
                    findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
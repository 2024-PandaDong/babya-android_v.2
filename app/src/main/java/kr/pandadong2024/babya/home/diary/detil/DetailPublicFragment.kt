package kr.pandadong2024.babya.home.diary.detil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDetailPublicBinding
import kr.pandadong2024.babya.home.diary.CommentsAdapter
import kr.pandadong2024.babya.util.BottomControllable

class DetailPublicFragment : Fragment() {
    private var _binding: FragmentDetailPublicBinding? = null
    private val binding get() = _binding!!

    private lateinit var  commentsAdapter : CommentsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailPublicBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        initCommentRecyclerView()
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailPublicFragment_to_diaryFragment)
        }
        return binding.root
    }

    private fun initCommentRecyclerView(){
        val testList = mutableListOf<String>()
        for (i in 0.. 100){
            testList.add(i.toString())
        }
        commentsAdapter = CommentsAdapter(testList)
        with(binding){
            commentRecyclerView.adapter = commentsAdapter
        }


    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
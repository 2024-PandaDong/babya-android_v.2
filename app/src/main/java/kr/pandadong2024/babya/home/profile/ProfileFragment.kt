package kr.pandadong2024.babya.home.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentProfileBinding
import kr.pandadong2024.babya.home.profile.adapter.ProfileBoardAdapter
import kr.pandadong2024.babya.home.profile.adapter.ProfileDiaryAdapter
import kr.pandadong2024.babya.home.profile.data.ProfileBoardData
import kr.pandadong2024.babya.home.profile.data.ProfileDiaryData
import kr.pandadong2024.babya.server.RetrofitBuilder


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"
    private val email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // 정보 받기
        kotlin.runCatching {
            getProfileData()
            profileDiary()
            profileBoard()
        }

        // 즐겨찾기 화면으로 이동
        binding.bookmarkBtn.setOnClickListener {
            goBookmark()
        }




        return binding.root
    }

    // 개시판 정보 받기
    private fun profileBoard() {
        /** 더미데이터 나중에 삭제해야함*/
        val boardList = ArrayList<ProfileBoardData>()

        boardList.add(ProfileBoardData("3/14", "1이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("3/24", "2이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("4/11", "3이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("4/12", "4이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("5/13", "5이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("6/16", "6이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("7/17", "7이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("10/18", "8이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("11/19", "9이 심장소리 들어본날"))
        boardList.add(ProfileBoardData("12/19", "10이 심장소리 들어본날"))
        /** ============================================== */
        val adapter = ProfileBoardAdapter(boardList)
        binding.boardRv.adapter = adapter
        binding.boardRv.layoutManager = LinearLayoutManager(requireContext())

    }

    // 산모일기 정보 받기
    private fun profileDiary() {

        /** 더미데이터 나중에 삭제해야함*/
        val diaryList = ArrayList<ProfileDiaryData>()

        diaryList.add(ProfileDiaryData("3/14", "1이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("3/24", "2이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("4/11", "3이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("4/12", "4이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("5/13", "5이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("6/16", "6이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("7/17", "7이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("10/18", "8이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("11/19", "9이 심장소리 들어본날"))
        diaryList.add(ProfileDiaryData("12/19", "10이 심장소리 들어본날"))
        /** ============================================== */
        val adapter = ProfileDiaryAdapter(diaryList)
        binding.diaryRv.adapter = adapter
        binding.diaryRv.layoutManager = LinearLayoutManager(requireContext())
    }


    // 프로필 정보 받기
    private fun getProfileData() {
        /** 프로필 정보 보여주기
         * email을 넣어야함*/
//        lifecycleScope.launch(Dispatchers.IO){
//            kotlin.runCatching {
//                RetrofitBuilder.getProfileService().getProfile(
//
//                    email = email
//                )
//            }.onSuccess { value ->
//
//
//            }.onFailure {
//                it.printStackTrace()
//                Log.d(TAG, "onCreateView: 서버연결 실패")
//            }
//        }
    }


    // 즐겨찾기 화면으로 이동
    private fun goBookmark() {
        findNavController().navigate(R.id.action_profileFragment_to_bookmarkFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
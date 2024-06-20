package kr.pandadong2024.babya.home.dash_board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDashBoardBinding
import kr.pandadong2024.babya.databinding.FragmentProfileBinding
import kr.pandadong2024.babya.home.profile.adapter.ProfileBoardAdapter
import kr.pandadong2024.babya.home.profile.data.ProfileBoardData
import kr.pandadong2024.babya.util.BottomControllable


class DashBoardFragment : Fragment() {

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)

        // 정보 받기
        kotlin.runCatching {
            getBoardData()
        }


        return binding.root
    }

    private fun getBoardData() {
        /** 더미데이터 나중에 삭제해야함*/
        val DashBoardList = ArrayList<DashBoardData>()

        DashBoardList.add(DashBoardData("임산부 취미 공유합니다!", "동동이맘", "오늘부터 4주차 동동이 엄마 입니다.\n 임신한 후에 모임 가지면서 요가 같이 해봐요~", "30분전", "74", "2"))
        DashBoardList.add(DashBoardData("임산부 취미 공유합니다!", "동동이맘", "오늘부터 4주차 동동이 엄마 입니다.\n 임신한 후에 모임 가지면서 요가 같이 해봐요~", "30분전", "74", "2"))
        DashBoardList.add(DashBoardData("임산부 취미 공유합니다!", "동동이맘", "오늘부터 4주차 동동이 엄마 입니다.\n 임신한 후에 모임 가지면서 요가 같이 해봐요~", "30분전", "74", "2"))
        DashBoardList.add(DashBoardData("임산부 취미 공유합니다!", "동동이맘", "오늘부터 4주차 동동이 엄마 입니다.\n 임신한 후에 모임 가지면서 요가 같이 해봐요~", "30분전", "74", "2"))
        DashBoardList.add(DashBoardData("임산부 취미 공유합니다!", "동동이맘", "오늘부터 4주차 동동이 엄마 입니다.\n 임신한 후에 모임 가지면서 요가 같이 해봐요~", "30분전", "74", "2"))
        DashBoardList.add(DashBoardData("임산부 취미 공유합니다!", "동동이맘", "오늘부터 4주차 동동이 엄마 입니다.\n 임신한 후에 모임 가지면서 요가 같이 해봐요~", "30분전", "74", "2"))
        DashBoardList.add(DashBoardData("임산부 취미 공유합니다!", "동동이맘", "오늘부터 4주차 동동이 엄마 입니다.\n 임신한 후에 모임 가지면서 요가 같이 해봐요~", "30분전", "74", "2"))

        /** ============================================== */
        val adapter = DashBoardAdapter(DashBoardList)
        binding.dashBoardRv.adapter = adapter
        binding.dashBoardRv.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}
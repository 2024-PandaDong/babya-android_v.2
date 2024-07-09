package kr.pandadong2024.babya.home.dash_board

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDashBoardBinding
import kr.pandadong2024.babya.home.dash_board.adapter.DashBoardAdapter
import kr.pandadong2024.babya.home.dash_board.dash_boardViewModel.DashBoardViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.dash_board.DashBoardResponses
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException


class DashBoardFragment : Fragment() {

    private var _binding: FragmentDashBoardBinding? = null
    private var dashBoardList: List<DashBoardResponses>? = null
    private lateinit var dashBoardAdapter: DashBoardAdapter
    private val viewModel by activityViewModels<DashBoardViewModel>()

    private val binding get() = _binding!!
    private val TAG = "DashBoardFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)


        binding.boardEditFloatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_dashBoardFragment_to_editDashBoardFragment)
        }

        binding.dashBoardBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_mainFragment)
        }


        return binding.root
    }

    private fun dashBoardRecyclerView() {
        Log.d(TAG, "dashBoardRecyclerView: ${dashBoardList}")

        dashBoardAdapter = DashBoardAdapter(dashBoardList!!){postId->
            lifecycleScope.launch(Dispatchers.Main){
                Log.d(TAG, "dashBoardRecyclerView: ${postId}")
                kotlin.runCatching {
                    viewModel.id.value = postId
                    findNavController().navigate(R.id.action_dashBoardFragment_to_detailDashBoardFragment)
                }
            }
        }
        dashBoardAdapter.notifyDataSetChanged()
        binding.dashBoardRv.adapter = dashBoardAdapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바를 초기화하고 설정
        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.boardToolbar)
        // 툴바에 메뉴를 인플레이트
        toolbar.inflateMenu(R.menu.dash_board_menu)

        // 메뉴 항목 클릭 이벤트 처리
        toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId) {

                R.id.question -> {
                    Log.d(TAG, "onCreateView: 선택1")
                    getDashBoardData(1, 10, 1)
                    true
                }
                R.id.community -> {
                    Log.d(TAG, "onCreateView: 선택2")
                    getDashBoardData(1, 10, 2)
                    true
                }
                R.id.daily -> {
                    Log.d(TAG, "onCreateView: 선택3")
                    getDashBoardData(1, 10, 3)
                    true
                }
                else -> false
            }
        }
    }



    // type | 0 : all | 1 : question | 2 : community | 3 : daily
    private fun getDashBoardData(page: Int, size: Int, type : Int) {
        Log.d(TAG, "getDashBoardData: pagd : ${page} size : ${size} type : ${type}")
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                var DashBoardData : BaseResponse<List<DashBoardResponses>>? = null
                val token = BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken.toString()

                Log.d(TAG, "getDashBoardData: start token : ${token}")
                when(type){
                    1 -> {
                        var dbs = RetrofitBuilder.getDashBoardService();
                        Log.d(TAG, "getDashBoardData: dbs : ${dbs}")


                        val dbsDt = dbs.getDashBoardList(
                            accessToken = "Bearer ${token}",
                            page = page,
                            size = size,
                            category = "1"
                        )
                        Log.d(TAG, "getDashBoardData: dbsDt : ${dbsDt}")
                        DashBoardData = RetrofitBuilder.getDashBoardService().getDashBoardList(
                            accessToken = "Bearer ${token}",
                            page = page,
                            size = size,
                            category = "1"
                        )
                        Log.e(TAG, "status : ${DashBoardData.status}, message : ${DashBoardData.message}")
                    }
                    2 -> {
                        DashBoardData = RetrofitBuilder.getDashBoardService().getDashBoardList(
                            accessToken = "Bearer ${token}",
                            page = page,
                            size = size,
                            category = "2"
                        )
                        Log.e(TAG, "status : ${DashBoardData.status}, message : ${DashBoardData.message}")
                    }
                    3 -> {
                        DashBoardData = RetrofitBuilder.getDashBoardService().getDashBoardList(
                            accessToken = "Bearer ${token}",
                            page = page,
                            size = size,
                            category = "3"
                        )
                        Log.e(TAG, "status : ${DashBoardData.status}, message : ${DashBoardData.message}")
                    }

                    else -> {
                        DashBoardData = RetrofitBuilder.getDashBoardService().getDashBoardList(
                            accessToken = "Bearer ${token}",
                            page = page,
                            size = size,
                            category = "1"
                        )
                        Log.e(TAG, "status : ${DashBoardData.status}, message : ${DashBoardData.message}"
                        )
                    }
                }
                Log.d(TAG, "Test12123123213")
                Log.e(TAG, "status : ${DashBoardData?.status}, message : ${DashBoardData?.message}")
                dashBoardList = DashBoardData?.data
            }.onSuccess {

                Log.d(TAG, "getDashBoardData: ${dashBoardList}")

                Log.d(TAG, "getDashBoardData: 성공")
                lifecycleScope.launch(Dispatchers.Main) {
                    dashBoardRecyclerView()
                }
            }.onFailure {
                it.printStackTrace()
                if (it is HttpException) {
                    val errorBody = it.response()?.raw()?.request
                    Log.e(TAG, "Error body: $errorBody")
                }

                dashBoardList = listOf(
                    DashBoardResponses()
                )
                Log.d(TAG, "getDashBoardData: 실패")
                lifecycleScope.launch(Dispatchers.Main) {
                    dashBoardRecyclerView()
                }
            }
        }
    }





    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)

    }
}
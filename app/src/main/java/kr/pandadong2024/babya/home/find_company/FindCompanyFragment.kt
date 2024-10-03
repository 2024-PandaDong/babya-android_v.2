package kr.pandadong2024.babya.home.find_company

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentFindCompanyBinding
import kr.pandadong2024.babya.home.find_company.adapter.FindCompanyAdapter
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.remote.responses.BaseResponse
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException

class FindCompanyFragment : Fragment() {
    private var _binding: FragmentFindCompanyBinding? = null
    private val binding get() = _binding!!
    private var companyList: List<CompanyListResponses>? = null
    private lateinit var companyAdapter: FindCompanyAdapter
    private val viewModel by activityViewModels<FindCompanyViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindCompanyBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_findCompanyFragment_to_mainFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener (
            SwipeRefreshLayout.OnRefreshListener {
                findCompany()
            }
        )

        kotlin.run {
            findCompany()
        }


        return  binding.root
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    private fun findCompany(){
        lifecycleScope.launch(Dispatchers.IO){
            kotlin.runCatching {
                var companyData : BaseResponse<List<kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses>>? = null
                val token = BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken
                companyData = RetrofitBuilder.getCompanyService().getCompanyList(
                    accessToken = "Bearer $token",
                    page = 1,
                    size = 100
                )

                companyList = companyData.data
            }.onSuccess {
                lifecycleScope.launch(Dispatchers.Main) {
                    companyRecyclerView()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }.onFailure {
                it.printStackTrace()
                if (it is HttpException) {
                    val errorBody = it.response()?.raw()?.request
                    Log.e(TAG, "Error body: $errorBody")
                }

                companyList = listOf(
                    CompanyListResponses()
                )
                Log.d(TAG, "getDashBoardData: 실패")
                lifecycleScope.launch(Dispatchers.Main) {
                    companyRecyclerView()
                }
            }
        }
    }

    private fun companyRecyclerView() {
        companyAdapter = FindCompanyAdapter(companyList!!){ id ->
            lifecycleScope.launch(Dispatchers.Main){
                kotlin.runCatching {
                    viewModel.id.value = id
                    findNavController().navigate(R.id.action_findCompanyFragment_to_companyDetailsFragment)
                }
            }
        }
        companyAdapter.notifyDataSetChanged()
        binding.findCompanyRecyclerView.adapter = companyAdapter
    }
}


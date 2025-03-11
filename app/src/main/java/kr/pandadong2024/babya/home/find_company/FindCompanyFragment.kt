package kr.pandadong2024.babya.home.find_company

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentFindCompanyBinding
import kr.pandadong2024.babya.home.find_company.adapter.FindCompanyAdapter
import kr.pandadong2024.babya.home.find_company.find_company_viewModel.FindCompanyViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.remote.responses.company.CompanyListResponses
import kr.pandadong2024.babya.util.BottomControllable

@AndroidEntryPoint
class FindCompanyFragment : Fragment() {
    private var _binding: FragmentFindCompanyBinding? = null
    private val binding get() = _binding!!
    private var companyList: List<CompanyListResponses>? = null
    private lateinit var companyAdapter: FindCompanyAdapter
    private val viewModel : FindCompanyViewModel by viewModels()
    private lateinit var token : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindCompanyBinding.inflate(inflater, container, false)
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)

        lifecycleScope.launch {
            token = viewModel.getToken()?.accessToken.toString()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_findCompanyFragment_to_mainFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener(
            SwipeRefreshLayout.OnRefreshListener {
                viewModel.initCompany()
            }
        )

        kotlin.run {
            findName()
        }

        viewModel.companyList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                companyRecyclerView(it)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.lastCompanyPosition.observe(viewLifecycleOwner) {
            Log.d("test", "last : $it")
            binding.findCompanyRecyclerView.scrollToPosition(it)
        }

        binding.findCompanyRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (
                        !binding.findCompanyRecyclerView.canScrollVertically(1) &&
                        newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        viewModel.addCompany()
                    }
                }
            }
        )

        return binding.root
    }

    private fun findName() {
        lifecycleScope.launch() {
            kotlin.runCatching {
                RetrofitBuilder.getProfileService().getProfile(
                    accessToken = "Bearer $token",
                    email = "my"
                )
            }.onSuccess { result ->
                binding.welcomeName.text = result.data?.nickname + "님과 맞는 회사를 찾아보세요!"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    private fun companyRecyclerView(companyList: List<CompanyListResponses>) {
        companyAdapter = FindCompanyAdapter(companyList) { id, position ->
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.setLastPosition(position)
                viewModel.id.value = id
                findNavController().navigate(R.id.action_findCompanyFragment_to_companyDetailsFragment)
            }
        }
        companyAdapter.notifyItemRemoved(0)
        companyAdapter.notifyDataSetChanged()
        binding.findCompanyRecyclerView.adapter = companyAdapter
    }
}


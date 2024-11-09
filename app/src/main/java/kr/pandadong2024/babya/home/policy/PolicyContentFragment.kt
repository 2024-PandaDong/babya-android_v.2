package kr.pandadong2024.babya.home.policy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.databinding.FragmentPolicyContentBinding
import kr.pandadong2024.babya.home.policy.dialog.DeclarationDialog
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.util.BottomControllable
import kr.pandadong2024.babya.util.shortToast
import retrofit2.HttpException
import java.io.File


class PolicyContentFragment : Fragment() {
    var _binding: FragmentPolicyContentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<PolicyViewModel>()
    val TAG = "PolicyContentFragment"
    var policyLink: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyContentBinding.inflate(inflater, container, false)
        viewModel.policyList.value?.get(viewModel.policyId.value ?: -1)?.policyId?.let {
            setScreen(
                it
            )
        }

        viewModel.tagsList.observe(viewLifecycleOwner){
            binding.policyOrganTitleText.visibility = View.VISIBLE
            binding.policyOrganText.visibility = View.VISIBLE
            binding.policyOrganText.text = "${viewModel.tagsList.value?.get(0) ?: ""} ${viewModel.tagsList.value?.get(1) ?: ""}"
        }
        binding.signUpBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.declarationButton.setOnClickListener {
            val dialog = DeclarationDialog()
            dialog.isCancelable = false

            dialog.show(requireActivity().supportFragmentManager, dialog.tag)

        }

        binding.linkButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyLink))
            try {
                startActivity(intent)
            }
            catch (e : Exception){
                requireContext().shortToast("플레이 스토어에 승인을 받아주십시오")
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
    }

    private fun setScreen(id: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                RetrofitBuilder.getPolicyService().getPolicyContent(id)
            }.onSuccess { result ->
                if (result.status == 200) {
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "200,\nstatus : ${result.data}")
                        binding.policyTitleText.text = result.data?.title

                        if (result.data?.editDate == null) {
                            binding.policyDateRangeText.visibility = View.GONE
                        } else {
                            binding.policyDateRangeText.text = "최종수정일: ${
                                result.data?.editDate.substring(
                                    startIndex = 5,
                                    endIndex = 7
                                )
                            }월 ${result.data?.editDate?.substring(startIndex = 8, endIndex = 10)}일"
                        }
                        Log.d("policyLink", "policyLink : ${result.data?.link}")
                        if (result.data?.link != null || result.data?.link != ""){
                            policyLink = result.data?.link.toString()
                        }else{
                            binding.linkButton.visibility = View.GONE
                        }
                        var tagSample = result.data?.content.toString().indexOf("<table ")
                        var tabRes = result.data?.content.toString()
                            .replace("<td", "<td style='border : 1px solid black '")
                        tabRes = tabRes
                            .replace("<thead", "\u2664")
                            .replace("<th", "<th style='border : 1px solid black '")
                            .replace("\u2664", "<thead")
                        tabRes =
                            tabRes.replace("<table", "<table style='border-collapse : collapse; '")

                        binding.htmlLodeText.loadDataWithBaseURL(
                            null,
                            tabRes,
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                } else {
                    Log.d(TAG, "200이 아닌 다른 상태,\nstatus : ${result.status}")
                }
            }.onFailure { result ->
                result.printStackTrace()
                Log.e(TAG, "result = ${result.message}")
                if (result is HttpException) {
                    val errorBody = result.response()?.errorBody()?.string()
                    Log.e(TAG, "Error body: $errorBody")
                }
            }

        }
    }


    fun listFilesInInternalStorage(context: Context) {
        val directory: File = context.filesDir
        val files: Array<File>? = directory.listFiles()

        if (files != null) {
            for (file in files) {
                println("File: ${file.name}")
            }
        } else {
            println("No files found in the directory.")
        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }

    fun getFileUri(context: Context, file: File): Uri? {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }


}
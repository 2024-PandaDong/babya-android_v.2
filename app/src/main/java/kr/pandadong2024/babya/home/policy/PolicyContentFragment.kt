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
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentPolicyContentBinding
import kr.pandadong2024.babya.home.policy.dialog.DeclarationDialog
import kr.pandadong2024.babya.home.policy.viewmdole.PolicyViewModel
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.util.BottomControllable
import retrofit2.HttpException
import java.io.File


class PolicyContentFragment : Fragment() {
    var _binding: FragmentPolicyContentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<PolicyViewModel>()
    val TAG = "PolicyContentFragment"
    var policyLink : String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPolicyContentBinding.inflate(inflater, container, false)
        viewModel.policyList.value!![viewModel.policyId.value!!].policyId?.let { setScreen(it) }
        binding.signUpBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_policyContentFragment_to_policyMainFragment)
        }

        binding.declarationButton.setOnClickListener {
            val dialog =  DeclarationDialog()
            dialog.isCancelable = false

            dialog.show(requireActivity().supportFragmentManager, dialog.tag)

        }

        binding.linkButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyLink))
            startActivity(intent)
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
//                    val file = File(context?.filesDir, "index.html")
//                    if(file.exists()){
//                        FileOutputStream(file).use { output ->
//                            output.write("".toByteArray())  // 빈 문자열을 씀
//                        }
//                    }
//                    else{
//                        file.createNewFile()
//                    }
//                    listFilesInInternalStorage(requireContext())
//
//                    val html = Jsoup.parse(result.data!!.content).html()
//                    FileOutputStream(file).use { output ->
//                        output.write(html.toByteArray())
//                    }
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "200,\nstatus : ${result.data}")
                        policyLink = result.data!!.link
                        binding.policyTitleText.text = result.data.title

                        binding.policyDateRangeText.text = "최종수정일: ${result.data.editDate.substring(startIndex = 5, endIndex = 7)}월 ${result.data.editDate.substring(startIndex = 8, endIndex = 10)}일"
                        binding.htmlLodeText.loadDataWithBaseURL(null,result.data.content,"text/html","utf-8",null )


//                        binding.htmlLodeText.addInternalLinkPrefix(result.data.content)


                        val filePath = requireContext().getExternalFilesDir(null)?.absolutePath
                        Log.d(TAG, "Internal File Path: $filePath")


//                        var prefix: String = indexUrl.toString()
//                        Log.d(TAG, prefix)
//                        val cut = prefix.lastIndexOf('/')
//                        prefix = prefix.substring(0, cut + 1)
//                        binding.htmlLodeText.addInternalLinkPrefix(prefix)
//                        binding.htmlLodeText.loadHtml(indexUrl)


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
package kr.pandadong2024.babya.home.dash_board

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentEditDashBoardBinding
import kr.pandadong2024.babya.server.RetrofitBuilder
import kr.pandadong2024.babya.server.local.BabyaDB
import kr.pandadong2024.babya.server.local.TokenDAO
import kr.pandadong2024.babya.server.remote.request.dash_board.DashBoardRequest
import kr.pandadong2024.babya.util.BottomControllable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class EditDashBoardFragment : Fragment() {
    private var _binding: FragmentEditDashBoardBinding? = null
    private val binding get() = _binding!!
    private var selectedCategory: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var getImage: ActivityResultLauncher<String>
    private var title: String? = null
    private var content: String? = null

    private lateinit var tokenDao: TokenDAO

    private val TAG = "EditDashBoardFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentEditDashBoardBinding.inflate(inflater, container, false)

        tokenDao = BabyaDB.getInstance(requireContext().applicationContext)?.tokenDao()!!

        binding.dashBoardBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_editDashBoardFragment_to_dashBoardFragment)
        }

        binding.editDashBoardAddImageCardView.setOnClickListener {
            getImage.launch("image/*")
        }

        binding.questionCategory.setOnClickListener {
            Log.d(TAG, "onCreateView: 1선택")
            selectCategory("1")
        }

        binding.communityCategory.setOnClickListener {
            Log.d(TAG, "onCreateView: 2선택")
            selectCategory("2")
        }

        binding.diaryCategory.setOnClickListener {
            Log.d(TAG, "onCreateView: 3선택")
            selectCategory("3")
        }

        binding.editDashBoardSubmitButton.setOnClickListener {
            title = binding.title.text.toString()
            content = binding.content.text.toString()

            Log.d(TAG, "onCreateView: 올리기")
            if (title != null && content != null && selectedCategory != null) {
                createDashBoard()
            }
            if (selectedCategory == null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "카테고리를 선택해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            if (title == null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            if (content == null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return binding.root
    }

    private fun createDashBoard() {
        title = binding.title.text.toString()
        content = binding.content.text.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            val token = BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken.toString()
            kotlin.runCatching {
                RetrofitBuilder.getDashBoardService().postDashBoard(
                    accessToken = "Bearer ${token}",
                    body = DashBoardRequest(
                        title = title!!,
                        content = content!!,
                        category = selectedCategory!!,
                        file = uploadFile(listOf(selectedImageUri!!))
                    )
                )
            }.onSuccess {
                Log.d(TAG, "createDashBoard: 게시물 올리기 성공!!")
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.action_editDashBoardFragment_to_dashBoardFragment)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun uploadFile(listOf: List<Uri>): List<String> {
        val imageLinkList = mutableListOf<String>()
        val job = lifecycleScope.launch(Dispatchers.IO) {
            for (uri in listOf) {
                val token = BabyaDB.getInstance(requireContext())?.tokenDao()?.getMembers()?.accessToken.toString()
                kotlin.runCatching {
                    RetrofitBuilder.getCommonService().fileUpload(
                        accessToken = "Bearer ${token}",
                        file = prepareFilePart(uri)!!
                    )
                }.onSuccess { result ->
                    imageLinkList.add(result.data!!)
                }.onFailure { result ->
                    result.printStackTrace()
                    Log.e(TAG, "result = ${result.message}")
                }
            }
        }
        runBlocking {
            job.join()
        }
        return imageLinkList.toList()
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "temp_file")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun prepareFilePart(uri: Uri): MultipartBody.Part? {
        val file = getFileFromUri(uri = uri)
        return file?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", it.name, requestFile)
        }
    }

    private fun selectCategory(category: String) {
        when (category) {
            "1" -> {
                binding.questionCategory.isEnabled = true
                binding.communityCategory.isEnabled = false
                binding.diaryCategory.isEnabled = false
            }
            "2" -> {
                binding.questionCategory.isEnabled = false
                binding.communityCategory.isEnabled = true
                binding.diaryCategory.isEnabled = false
            }
            "3" -> {
                binding.questionCategory.isEnabled = false
                binding.communityCategory.isEnabled = false
                binding.diaryCategory.isEnabled = true
            }
        }

        selectedCategory = category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                if (uri == null) {
                    binding.selectedImage.setImageURI(selectedImageUri)
                    if (selectedImageUri == null) {
                        binding.iconImage.visibility = View.VISIBLE
                    }
                } else {
                    binding.iconImage.visibility = View.GONE
                    binding.selectedImage.setImageURI(uri)
                    selectedImageUri = uri
                }
            }
        )
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
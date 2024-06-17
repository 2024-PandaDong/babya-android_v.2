package kr.pandadong2024.babya.home.diary

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import kr.pandadong2024.babya.R
import kr.pandadong2024.babya.databinding.FragmentDiaryBinding
import kr.pandadong2024.babya.databinding.FragmentEditDiaryBinding
import kr.pandadong2024.babya.util.BottomControllable

class EditDiaryFragment : Fragment() {
    private var _binding: FragmentEditDiaryBinding? = null
    private val binding get() = _binding!!
    private val TAG = "EditDiaryFragment"
    private var selectedImageUri : Uri? = null
    private lateinit var getImage: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        (requireActivity() as BottomControllable).setBottomNavVisibility(false)
        _binding = FragmentEditDiaryBinding.inflate(inflater, container, false)
        binding.editDiaryAddImageCardView.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.diaryBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_editDiaryFragment_to_diaryFragment)
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getImage = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback{
                        uri ->
                    Log.d(TAG, uri.toString())
                    if (uri == null){
                        binding.selectedImage.setImageURI(selectedImageUri)
                        if(selectedImageUri == null){
                            binding.iconImage.visibility = View.VISIBLE
                        }
                    }
                    else{
                        binding.iconImage.visibility = View.GONE
                        binding.selectedImage.setImageURI(uri)
                        selectedImageUri = uri
                    }

                }
            )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as BottomControllable).setBottomNavVisibility(true)
    }
}
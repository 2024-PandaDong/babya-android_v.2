package kr.pandadong2024.babya.home.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kr.pandadong2024.babya.databinding.FragmentBookmarkBinding
import kr.pandadong2024.babya.home.profile.data.BookmarkData

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        /** 툴바 설정*/
        val toolbar: Toolbar = binding.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        kotlin.runCatching {
            bookmarkRv()
        }


        return binding.root
    }

    private fun bookmarkRv() {
        /** 더미데이터 나중에 삭제해야함*/
        val bookmarkList = ArrayList<BookmarkData>()

        bookmarkList.add(BookmarkData("1이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("2이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("3이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("4이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("5이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("6이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("7이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("8이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("9이 심장소리 들어본날"))
        bookmarkList.add(BookmarkData("10이 심장소리 들어본날"))
        /** ============================================== */
        val adapter = BookmarkAdapter(bookmarkList)
        binding.bookmarkRv.adapter = adapter
        binding.bookmarkRv.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed() // 툴바 뒤로가기
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

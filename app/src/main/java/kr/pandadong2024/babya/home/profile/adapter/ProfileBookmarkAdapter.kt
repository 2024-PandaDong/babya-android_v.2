package kr.pandadong2024.babya.home.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kr.pandadong2024.babya.databinding.ItemBookmarkGridviewBinding
import kr.pandadong2024.babya.home.profile.data.ProfileBoardData
import kr.pandadong2024.babya.home.profile.data.ProfileBookmarkData

class ProfileBookmarkAdapter:BaseAdapter() {
    private var itemList = mutableListOf<ProfileBookmarkData>()
    override fun getCount(): Int = 6

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setItems(mutableList: MutableList<ProfileBookmarkData>){
        itemList = mutableList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemBookmarkGridviewBinding
        val view: View

        if (convertView == null) {
            binding = ItemBookmarkGridviewBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemBookmarkGridviewBinding
        }

        val data = itemList[position]
        binding.name.text = data.name

        return view
    }

}
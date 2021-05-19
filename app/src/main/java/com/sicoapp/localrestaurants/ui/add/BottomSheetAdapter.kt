package com.sicoapp.localrestaurants.ui.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.domain.SdStoragePhoto


class BottomSheetAdapter (private val sdStoragePhoto : List<SdStoragePhoto>): RecyclerView.Adapter<BottomSheetAdapter.Item>() {

    private var list = mutableListOf<SdStoragePhoto>()

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Item, position: Int) {
        holder.tv.text = list[position].toString()
        holder.iv.setImageBitmap(list[position].bmp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(LayoutInflater.from(parent.context).inflate(R.layout.view_holder, parent, false))
    }

    class Item(view: View) : RecyclerView.ViewHolder(view) {
        val tv = view.findViewById<TextView>(R.id.tv_view_holder)!!
        val iv = view.findViewById<ImageView>(R.id.iv_view_holder)!!
    }

    fun addRestaurantToAdapter(){
        list = sdStoragePhoto.toMutableList()
        notifyDataSetChanged()
    }
}
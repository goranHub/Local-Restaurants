package com.sicoapp.localrestaurants.ui.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sicoapp.localrestaurants.R


class BottomSheetAdapter : RecyclerView.Adapter<BottomSheetAdapter.Item>() {

    private val list = (1..15).toList()

    override fun getItemId(position: Int) = list[position].toLong()

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Item, position: Int) {
        holder.tv.text = list[position].toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(LayoutInflater.from(parent.context).inflate(R.layout.view_holder, parent, false))
    }

    class Item(view: View) : RecyclerView.ViewHolder(view) {
        val tv = view.findViewById<TextView>(R.id.tv_view_holder)!!
        val iv = view.findViewById<ImageView>(R.id.iv_view_holder)!!
    }
}
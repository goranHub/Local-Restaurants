package com.sicoapp.localrestaurants.ui.all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sicoapp.localrestaurants.databinding.ItemBottomSheetBinding
import javax.inject.Inject


class BottomSheetAdapter @Inject constructor(): RecyclerView.Adapter<BottomSheetAdapter.BottomViewHolder>() {

    private var list = mutableListOf<BindSdStoragePhoto>()

    lateinit var binding : ItemBottomSheetBinding

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BottomViewHolder, position: Int) {
        val dataModel = list[position]
        holder.bind(dataModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ItemBottomSheetBinding.inflate(layoutInflater, parent, false)
        return BottomViewHolder(binding)
    }

    class BottomViewHolder(val binding: ItemBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: Any?) {
            binding.setVariable(BR.data, obj)
            binding.executePendingBindings()
        }
    }

    fun addRestaurantToAdapter(sdStoragePhoto : List<BindSdStoragePhoto>){
        list = sdStoragePhoto.toMutableList()
        notifyDataSetChanged()
    }


}
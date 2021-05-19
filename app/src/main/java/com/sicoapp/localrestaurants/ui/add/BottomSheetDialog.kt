package com.sicoapp.localrestaurants.ui.add

import android.app.Dialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.domain.SdStoragePhoto
import kotlinx.android.synthetic.main.bottom_sheet.recyclerView


class BottomSheetDialog(private val photos: List<SdStoragePhoto>) : BottomSheetDialogFragment() {


    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.apply {
            setContentView(R.layout.bottom_sheet)
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = BottomSheetAdapter(photos)
            recyclerView.adapter = adapter
            adapter.addRestaurantToAdapter()
        }
    }
}
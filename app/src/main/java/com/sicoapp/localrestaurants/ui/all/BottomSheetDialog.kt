package com.sicoapp.localrestaurants.ui.all

import android.app.Dialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import com.sicoapp.localrestaurants.utils.bmpIsNotNull
import kotlinx.android.synthetic.main.bottom_sheet.recyclerView
import javax.inject.Inject


class BottomSheetDialog @Inject constructor(
    private val adapter: BottomSheetAdapter,
) : BottomSheetDialogFragment() {

    lateinit var photos: List<SdStoragePhoto>

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.apply {
            setContentView(R.layout.bottom_sheet)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            val mapToBind = photos
                .filter {
                    it.bmpIsNotNull()
                }.map {
                    BindSdStoragePhoto(it)
                }
            adapter.addRestaurantToAdapter(mapToBind)
        }
    }
}
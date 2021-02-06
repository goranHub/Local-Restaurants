package com.sicoapp.localrestaurants.utils

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.ui.map.MapViewModel
import kotlinx.android.synthetic.main.fragment_dialog_with_data.*
import kotlinx.android.synthetic.main.fragment_dialog_with_data.view.*


class DialogWithData : DialogFragment() {

    companion object {
        const val TAG = "DialogWithData"
    }

    private lateinit var viewModel: MapViewModel
    var texxt =""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_dialog_with_data, container, false)
        val tv = v.findViewById<EditText>(R.id.etName)
        tv.text = Editable.Factory.getInstance().newEditable(texxt)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupClickListeners(view: View) {
        view.btnSubmit.setOnClickListener {
            viewModel.sendName(view.etName.text.toString())
            dismiss()
        }
    }



}
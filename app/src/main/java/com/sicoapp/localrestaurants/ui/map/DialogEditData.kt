package com.sicoapp.localrestaurants.ui.map

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.sicoapp.localrestaurants.R
import kotlinx.android.synthetic.main.fragment_dialog_edit_data.view.*

/**
 * @author ll4
 * @date 2/6/2021
 */
class DialogEditData(val name: String) : DialogFragment()  {

    private lateinit var tv : EditText
    var listener : ListenerEditData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_dialog_edit_data, container, false)
        tv = v.findViewById(R.id.etName)
        tv.text = Editable.Factory.getInstance().newEditable(name)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            val text = tv.text.toString()
            listener?.onSubmitData(text)
            dismiss()
        }
    }
}

interface ListenerEditData {
    fun onSubmitData(submitData : String)
}

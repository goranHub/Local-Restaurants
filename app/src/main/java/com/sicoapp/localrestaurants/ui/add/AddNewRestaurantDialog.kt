package com.sicoapp.localrestaurants.ui.add

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.sicoapp.localrestaurants.R


/**
 * @author ll4
 * @date 5/15/2021
 */
class AddNewRestaurantDialog : DialogFragment() {

    lateinit var listener : ListenerClicked


    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.fragment_dialog_add_restaurant, null))

                .setPositiveButton(R.string.ok)
                { _, _ ->
                    val name = dialog!!.findViewById<View>(R.id.name) as EditText
                    val address = dialog!!.findViewById<View>(R.id.address) as EditText
                    listener.onName(name = name.text.toString())
                    listener.onAddress(address = address.text.toString())
                }
                .setNegativeButton(R.string.cancel)
                { _, _ ->
                    dialog?.cancel()
                }
                .setTitle("Add Restaurant")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface ListenerClicked {
        fun onName(name: String)
        fun onAddress(address: String)
    }
}
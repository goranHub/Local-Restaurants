package com.sicoapp.localrestaurants.ui.map.add

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.sicoapp.localrestaurants.R
import javax.inject.Inject


/**
 * @author ll4
 * @date 5/15/2021
 */
class AddNewRestaurantDialog @Inject constructor() : DialogFragment() {

    lateinit var listener: ListenerClicked


    @SuppressLint("InflateParams", "ShowToast")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.fragment_dialog_add_restaurant, null))

                .setPositiveButton(R.string.ok)
                { _, _ ->
                    val editTextName = dialog!!.findViewById<View>(R.id.name) as EditText
                    val editTextAddress = dialog!!.findViewById<View>(R.id.address) as EditText

                    val name = editTextName.text.toString()
                    val address = editTextAddress.text.toString()

                    if (name.length > 5) {
                        listener.onNewRestaurant(name, address)
                    } else {
                        Toast.makeText(dialog!!.context, "To short", Toast.LENGTH_LONG)
                    }
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
        fun onNewRestaurant(name: String, address: String)
    }
}
package com.sicoapp.localrestaurants.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.sicoapp.localrestaurants.R


/**
 * @author ll4
 * @date 1/26/2021
 */

abstract class BaseFR <T : ViewBinding, A : Any> : Fragment() {

    private var handler: A? = null //It's base activity

    protected open var binding: T? = null

    private lateinit var dialog: Dialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("UNCHECKED_CAST")
        this.handler = this.activity as? A
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = this.setBinding(inflater,container)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T

    open fun alertDialog(name: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Restaurant")
        builder.setMessage(name)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(
                requireContext(),
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(
                requireContext(),
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(
                requireContext(),
                "Maybe", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }

    open fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }

    open fun hideProgressDialog() {
        dialog.dismiss()
    }

}
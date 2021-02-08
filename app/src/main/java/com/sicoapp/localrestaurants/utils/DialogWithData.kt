package com.sicoapp.localrestaurants.utils



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.Restaurant
import kotlinx.android.synthetic.main.fragment_diralog_with_data.view.*

class DialogWithData(private val restaurant: Restaurant) : DialogFragment() {

    var listener : ListenerSubmitData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_diralog_with_data, container, false)

        val btTitle = v.findViewById<Button>(R.id.bt_title)
        val btAddress = v.findViewById<Button>(R.id.bt_address)
        val btLongitude = v.findViewById<Button>(R.id.bt_longitude)
        val btLatitude = v.findViewById<Button>(R.id.bt_latitude)

        btTitle.text = restaurant.name
        btAddress.text = restaurant.address
        btLongitude.text = restaurant.longitude
        btLatitude.text = restaurant.latitude

        return v
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListenersClose(view)
        setupClickListenersTitle(view)
        setupClickListenersAddress(view)
        setupClickListenersLongitude(view)
        setupClickListenersLatitude(view)
    }

    private fun setupClickListenersLatitude(view: View) {
        view.bt_latitude.setOnClickListener {
            val dialog = DialogEditData(restaurant.latitude.toString(), listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersLongitude(view: View) {
        view.bt_longitude.setOnClickListener {
            val dialog = DialogEditData(restaurant.longitude.toString(), listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersAddress(view: View) {
        view.bt_address.setOnClickListener {
            val dialog = DialogEditData(restaurant.address, listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersTitle(view: View) {
        view.bt_title.setOnClickListener {
            val dialog = DialogEditData(restaurant.name, listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersClose(view: View) {
        view.bt_close.setOnClickListener {
            dismiss()
        }
    }



}
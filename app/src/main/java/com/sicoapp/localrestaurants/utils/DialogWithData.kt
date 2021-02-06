package com.sicoapp.localrestaurants.utils



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse
import kotlinx.android.synthetic.main.fragment_diralog_with_data.view.*

class DialogWithData(private val restaurantResponse: RestaurantResponse ) : DialogFragment() {

    lateinit var listener : ListenerSubmitData


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

        btTitle.text = restaurantResponse.name
        btAddress.text = restaurantResponse.address
        btLongitude.text = restaurantResponse.longitude.toString()
        btLatitude.text = restaurantResponse.latitude.toString()

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
            val dialog = DialogEditData(restaurantResponse.latitude.toString(), listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersLongitude(view: View) {
        view.bt_longitude.setOnClickListener {
            val dialog = DialogEditData(restaurantResponse.longitude.toString(), listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersAddress(view: View) {
        view.bt_address.setOnClickListener {
            val dialog = DialogEditData(restaurantResponse.address, listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersTitle(view: View) {
        view.bt_title.setOnClickListener {
            val dialog = DialogEditData(restaurantResponse.name, listener)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun setupClickListenersClose(view: View) {
        view.bt_close.setOnClickListener {
            dismiss()
        }
    }



}
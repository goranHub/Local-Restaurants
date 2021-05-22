package com.sicoapp.localrestaurants.ui.map


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.local.storage.SdStoragePhoto
import com.sicoapp.localrestaurants.data.remote.Restraurant
import com.sicoapp.localrestaurants.ui.all.BindSdStoragePhoto
import kotlinx.android.synthetic.main.fragment_diralog_with_data.view.*
import javax.inject.Inject

class DialogWithData @Inject constructor() : DialogFragment() {

    var listener: ListenerClicked? = null
    var restaurant = Restraurant("", 0.0, 0.0, "", false)
    var bind: BindSdStoragePhoto? = null
    var mapToBind : SdStoragePhoto? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_diralog_with_data, container, false)

        val btTitle = view.findViewById<Button>(R.id.bt_name)
        val btAddress = view.findViewById<Button>(R.id.bt_address)
        val btLongitude = view.findViewById<Button>(R.id.bt_longitude)
        val btLatitude = view.findViewById<Button>(R.id.bt_latitude)
        val ivRestaurant = view.findViewById<ImageView>(R.id.iv_restaurant)

        btTitle.text = restaurant.name
        btAddress.text = restaurant.address
        btLongitude.text = restaurant.longitude.toString()
        btLatitude.text = restaurant.latitude.toString()

        if(mapToBind != null){
            val bmp = mapToBind!!.bmp
            ivRestaurant.setImageBitmap(bmp)
        }else{
            ivRestaurant.setImageResource(R.drawable.iv_bottom_sheet_restaurant)
        }

        return view
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
        setupClickListenersImageView(view)
    }


    private fun setupClickListenersLatitude(view: View) {
        view.bt_latitude.setOnClickListener {
            listener?.onButtonLatitude()
        }
    }

    private fun setupClickListenersLongitude(view: View) {
        view.bt_longitude.setOnClickListener {
            listener?.onButtonLongitude()
        }
    }

    private fun setupClickListenersAddress(view: View) {
        view.bt_address.setOnClickListener {
            listener?.onButtonAddress()
        }
    }

    private fun setupClickListenersTitle(view: View) {
        view.bt_name.setOnClickListener {
            listener?.onButtonName()
        }
    }

    private fun setupClickListenersImageView(view: View) {
        view.iv_restaurant.setOnClickListener {
            listener?.onImageView()
        }
    }

    private fun setupClickListenersClose(view: View) {
        view.bt_close.setOnClickListener {
            dialog?.dismiss()
            dismiss()
        }
    }


    interface ListenerClicked {
        fun onButtonLatitude()
        fun onButtonLongitude()
        fun onButtonAddress()
        fun onButtonName()
        fun onImageView()
    }


}
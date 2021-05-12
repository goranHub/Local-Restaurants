package com.sicoapp.localrestaurants.ui.map



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.domain.Restraurant
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_diralog_with_data.view.*

class DialogWithData(val restaurant: Restraurant) : DialogFragment() {

    var listener : ListenerClicked? = null
    lateinit var viewModel : MapViewModel
    private val disposables = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_diralog_with_data, container, false)

        val btTitle = v.findViewById<Button>(R.id.bt_name)
        val btAddress = v.findViewById<Button>(R.id.bt_address)
        val btLongitude = v.findViewById<Button>(R.id.bt_longitude)
        val btLatitude = v.findViewById<Button>(R.id.bt_latitude)
        val btPhoto = v.findViewById<Button>(R.id.bt_photo)

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
        setupClickListenersPhoto(view)
    }


    private fun setupClickListenersLatitude(view: View) {
        view.bt_latitude.setOnClickListener {
            listener?.onButton("latitude")
        }
    }

    private fun setupClickListenersLongitude(view: View) {
        view.bt_longitude.setOnClickListener {
            listener?.onButton("longitude")
        }
    }

    private fun setupClickListenersAddress(view: View) {
 /*       bt_address.clicks().subscribe {
            viewModel.addClicked()
        }.addTo(disposables)*/
        view.bt_address.setOnClickListener {
            listener?.onButton("address")
        }
    }

    private fun setupClickListenersTitle(view: View) {
        view.bt_name.setOnClickListener {
            listener?.onButton("name")
        }
    }

    private fun setupClickListenersPhoto(view: View) {
        view.bt_photo.setOnClickListener {
            listener?.onButton("photo")
        }
    }

    private fun setupClickListenersClose(view: View) {
        view.bt_close.setOnClickListener {
            listener?.onButton("close")
            dialog?.dismiss()
            dismiss()
        }
    }


    interface ListenerClicked {
        fun onButton(type: String)
    }


}
package com.sicoapp.localrestaurants.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Button
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.data.remote.response.RestaurantResponse

class CustomInfoWindow(
    val map: GoogleMap?,
    private val marker: Marker?,
    context: Context,
    baseLayout: ViewGroup
) {
    private var infoWindow: ViewGroup? = null
    private var custumMap: ViewGroup? = null
    private var closeBtn: Button? = null
    private var btnName: Button? = null
    private var btnAddress: Button? = null
    private var btnLongitude: Button? = null
    private var btnLatitude: Button? = null
    private var isShowInfo: Boolean = false
    private var animationEnable = true

    private var onCloseBtnClickListener: View.OnTouchListener? = null
        @SuppressLint("ClickableViewAccessibility")
        set(value) {
            closeBtn?.setOnTouchListener(value)
        }

    var onBtnNameClickListener: View.OnTouchListener? = null
        @SuppressLint("ClickableViewAccessibility")
        set(value) {
            btnName?.setOnTouchListener(value)
        }

    private val DURATION_WINDOW_ANIMATION = 400

    init {

        infoWindow =
            LayoutInflater
                .from(context)
                .inflate(R.layout.infowindow, null) as ViewGroup

        custumMap =
            infoWindow?.findViewById(R.id.fragment_custum_map)

        val response= marker?.tag as RestaurantResponse?

        btnName = infoWindow?.findViewById(R.id.bt_name)
        btnAddress = infoWindow?.findViewById(R.id.bt_address)
        btnLongitude = infoWindow?.findViewById(R.id.bt_longitude)
        btnLatitude = infoWindow?.findViewById(R.id.bt_latitude)
        closeBtn = infoWindow?.findViewById(R.id.bt_close)

        btnName?.text = response?.name
        btnAddress?.text = response?.address
        btnLongitude?.text = response?.latitude.toString()
        btnLatitude?.text = response?.longitude.toString()

        onCloseBtnClickListener = View.OnTouchListener { v, e -> infoWithAnimation(); false }

        baseLayout.addView(infoWindow)

    }


    private fun moveTo(position: Pair<Float, Float>?) {
        val (x, y) = position ?: return
        infoWindow?.x = x - (infoWindow?.width?.toFloat()?.div(2f) ?: 0f)
        infoWindow?.y = y - (custumMap?.height?.toFloat() ?: 0f) + ((btnName?.height?.toFloat()?.times(6.3f)) ?: 0f)
    }

    fun moveToMarkerPosition() {
        moveTo(getMarkerPosition())
    }

    fun toggleInfo() {
        if (isShowInfo) {
            infoWithAnimation()
        } else {
            showInfoAnimation()
        }
    }

    private fun infoWithAnimation() {
        infoWindow?.let {
            if (animationEnable) {
                val markerPosition = getMarkerPosition()
                if (markerPosition == null) {
                    hideInfo()
                } else {
                    val (centerContainerX, centerContainerY) = markerPosition
                    val hiddingAnimation = ScaleAnimation(
                        1f, 0f,
                        1f, 0f,
                        centerContainerX, centerContainerY
                    )

                    hiddingAnimation.duration = DURATION_WINDOW_ANIMATION.toLong()
                    hiddingAnimation.interpolator = DecelerateInterpolator()
                    hiddingAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation?) {
                        }
                        override fun onAnimationStart(animation: Animation) {
                        }
                        override fun onAnimationEnd(animation: Animation) {
                            hideInfo()
                        }
                    })
                    it.startAnimation(hiddingAnimation)
                }
            } else {
                hideInfo()
            }
        }
    }

    private fun showInfoAnimation() {
        infoWindow?.let {
            if (animationEnable) {
                val markerPosition = getMarkerPosition()
              if (markerPosition == null) {
                        showInfo()
                    } else {
                        val (centerContainerX, centerContainerY) = markerPosition
                        val showingAnimation = ScaleAnimation(
                        0f, 1f,
                        0f, 1f,
                        centerContainerX, centerContainerY
                    )

                    showingAnimation.duration = DURATION_WINDOW_ANIMATION.toLong()
                    showingAnimation.interpolator = DecelerateInterpolator()
                    showingAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation?) {
                        }
                        override fun onAnimationStart(animation: Animation) {
                        }
                        override fun onAnimationEnd(animation: Animation) {
                            showInfo()
                        }
                    })
                    it.startAnimation(showingAnimation)
                }
            } else {
                showInfo()
            }
        }
    }

    fun hideInfo() {
        infoWindow ?: return
        isShowInfo = false
        infoWindow?.visibility = View.INVISIBLE
        custumMap?.visibility = View.INVISIBLE
    }

    private fun showInfo() {
        infoWindow ?: return
        infoWindow?.visibility = View.VISIBLE
        isShowInfo = true
        custumMap?.visibility = View.VISIBLE
    }

    private fun getMarkerPosition(): Pair<Float, Float>? {
        val markerLocation = marker?.position ?: return null
        val projection = map?.projection ?: return null
        val screenPosition = projection.toScreenLocation(markerLocation)
        val markerX = screenPosition?.x?.toFloat() ?: 0f
        val markerY = screenPosition?.y?.toFloat() ?: 0f
        return Pair(markerX, markerY)
    }


}
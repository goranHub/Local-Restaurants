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
import com.google.android.gms.maps.model.MarkerOptions
import com.sicoapp.localrestaurants.R

@SuppressLint("ClickableViewAccessibility", "InflateParams")
class CustomInfoWindow(
    private val map: GoogleMap,
    val marker: MarkerOptions,
    private val context: Context,
    private val mapLayout: ViewGroup
) {
    lateinit var infoWindowForMarker: ViewGroup
    private lateinit var custumMap: ViewGroup
    private lateinit var closeBtn: Button
    private lateinit var btnName: Button
    private lateinit var btnAddress: Button
    private lateinit var btnLongitude: Button
    private lateinit var btnLatitude: Button
    private var isShowInfo: Boolean = false
    private var animationEnable = true
    private val DURATION_WINDOW_ANIMATION = 400

    private var onCloseBtnClickListener: View.OnTouchListener? = null
        @SuppressLint("ClickableViewAccessibility")
        set(value) {
            closeBtn.setOnTouchListener(value)
        }

    var onBtnNameClickListener: View.OnTouchListener? = null
        @SuppressLint("ClickableViewAccessibility")
        set(value) {
            btnName.setOnTouchListener(value)
        }

    init {
        populateMarkerWithInfoWindow(marker, mapLayout)
        onCloseBtnClickListener = View.OnTouchListener { _, _ -> hideInfoAnimation(); false }
    }

    private fun populateMarkerWithInfoWindow(
        marker: MarkerOptions,
        mapLayout: ViewGroup
    )
    {
        map.addMarker(marker)

        infoWindowForMarker = LayoutInflater
            .from(context)
            .inflate(R.layout.infowindow, null) as ViewGroup

        custumMap =
            infoWindowForMarker.findViewById(R.id.fragment_custum_map)

        btnName = infoWindowForMarker.findViewById(R.id.bt_name)
        btnAddress = infoWindowForMarker.findViewById(R.id.bt_address)
        btnLongitude = infoWindowForMarker.findViewById(R.id.bt_longitude)
        btnLatitude = infoWindowForMarker.findViewById(R.id.bt_latitude)
        closeBtn = infoWindowForMarker.findViewById(R.id.bt_close)

/*        btnName.text = response.name
        btnAddress.text = response.address
        btnLongitude.text = response.longitude.toString()
        btnLatitude.text = response.latitude.toString()*/

        btnName.text = marker.title
        btnLongitude.text = marker.position.longitude.toString()
        btnLatitude.text = marker.position.latitude.toString()

        infoWindowForMarker.visibility = View.INVISIBLE

        mapLayout.addView(infoWindowForMarker)

    }

    private fun getMarkerPosition(): Pair<Float, Float>? {
        val markerLocation = marker.position ?: return null
        val projection = map.projection ?: return null
        val screenPosition = projection.toScreenLocation(markerLocation)
        val markerX = screenPosition?.x?.toFloat() ?: 0f
        val markerY = screenPosition?.y?.toFloat() ?: 0f
        return Pair(markerX, markerY)
    }


    private fun moveTo(position: Pair<Float, Float>?) {
        val (x, y) = position ?: return
        infoWindowForMarker.x =
            x - (infoWindowForMarker.width.toFloat().div(2f))
        infoWindowForMarker.y =
            y - (custumMap.height.toFloat()) + ((btnName.height.toFloat().times(6.3f)))
    }

    fun toggleInfo() {
        if (isShowInfo) {
            hideInfoAnimation()
        } else {
            showInfoAnimation()
        }
    }

    private fun hideInfoAnimation() {
        infoWindowForMarker.let {
            if (animationEnable) {
                val markerPosition = getMarkerPosition()
                if (markerPosition == null) {
                    hideInfo()
                } else {
                    val (centerContainerX, centerContainerY) = markerPosition

                    val hiddingAnimation = ScaleAnimation(
                        1f, 0f,
                        1f, 0f,
                        centerContainerX,
                        centerContainerY
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
        infoWindowForMarker.let {
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
        isShowInfo = false
        infoWindowForMarker.visibility = View.INVISIBLE
        custumMap.visibility = View.INVISIBLE
    }

    private fun showInfo() {
        infoWindowForMarker.visibility = View.VISIBLE
        isShowInfo = true
        custumMap.visibility = View.VISIBLE
    }

}
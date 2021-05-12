package com.sicoapp.localrestaurants.ui.photo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.databinding.FragmentRestaurantPhotoBinding

/**
 * @author ll4
 * @date 3/24/2021
 */
class RestaurantPhotoFragment : Fragment() {

    lateinit var binding : FragmentRestaurantPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString("photo", "1")?.let {
            BindMyProfile().image = it
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_photo, container, false)
        return binding.root
    }
}
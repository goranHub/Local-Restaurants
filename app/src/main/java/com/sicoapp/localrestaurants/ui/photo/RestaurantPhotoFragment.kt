package com.sicoapp.localrestaurants.ui.photo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sicoapp.localrestaurants.databinding.FragmentRestaurantPhotoBinding


/**
 * @author ll4
 * @date 3/24/2021
 */
class RestaurantPhotoFragment : Fragment() {

    lateinit var binding: FragmentRestaurantPhotoBinding
    lateinit var imageString: String
    lateinit var imageStringConvert: String
    lateinit var bitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString("photo", "1")?.let {
            imageString = it
            imageStringConvert = "file:///sdcard"  +  imageString.substring(19)
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.parse(imageStringConvert))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRestaurantPhotoBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.restaurantImage.setImageBitmap(bitmap)

    }



}
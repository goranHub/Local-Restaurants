package com.sicoapp.localrestaurants.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.databinding.FragmentGalleryBinding
import com.sicoapp.localrestaurants.ui.BaseFR
import com.sicoapp.localrestaurants.ui.gallery.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SlideshowFragment : BaseFR() {

    private val viewModel: SlideshowViewModel by viewModels()
    private lateinit var binding: FragmentGalleryBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding =  FragmentGalleryBinding.bind(view)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val textView = binding.textGallery
        viewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return binding.root
    }
}
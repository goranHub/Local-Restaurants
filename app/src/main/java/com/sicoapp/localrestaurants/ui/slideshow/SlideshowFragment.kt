package com.sicoapp.localrestaurants.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.R
import com.sicoapp.localrestaurants.databinding.FragmentGalleryBinding
import com.sicoapp.localrestaurants.databinding.FragmentHomeBinding
import com.sicoapp.localrestaurants.databinding.FragmentSlideshowBinding
import com.sicoapp.localrestaurants.ui.Base
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SlideshowFragment :  Base<FragmentSlideshowBinding, BaseActivity>() {

    private val viewModel: SlideshowViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = setBinding(inflater,container)
        val textView = binding.textSlideshow
        viewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSlideshowBinding
            = FragmentSlideshowBinding.inflate(inflater, container, false)
}
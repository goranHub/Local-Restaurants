package com.sicoapp.localrestaurants.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.databinding.FragmentGalleryBinding
import com.sicoapp.localrestaurants.ui.Base
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment :  Base<FragmentGalleryBinding, BaseActivity>() {

    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = setBinding(inflater,container)
        val textView = binding.textGallery
        viewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGalleryBinding
    = FragmentGalleryBinding.inflate(inflater, container, false)

}
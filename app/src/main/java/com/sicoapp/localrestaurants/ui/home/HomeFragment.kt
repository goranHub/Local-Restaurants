package com.sicoapp.localrestaurants.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.databinding.FragmentHomeBinding
import com.sicoapp.localrestaurants.ui.Base
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Base<FragmentHomeBinding, BaseActivity>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = setBinding(inflater, container)
        val textView = binding.textHome
        viewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })



        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

}
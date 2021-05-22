package com.sicoapp.localrestaurants.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sicoapp.localrestaurants.BaseActivity
import com.sicoapp.localrestaurants.data.remote.Restraurant
import com.sicoapp.localrestaurants.databinding.FragmentDetailsBinding
import com.sicoapp.localrestaurants.ui.BaseFR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DetailsFragment : BaseFR<FragmentDetailsBinding, BaseActivity>() {

    private val viewModel: DetailsViewModel by viewModels()

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

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDetailsBinding =
        FragmentDetailsBinding.inflate(inflater, container, false)

}
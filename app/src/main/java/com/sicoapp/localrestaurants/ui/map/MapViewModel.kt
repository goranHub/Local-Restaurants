package com.sicoapp.localrestaurants.ui.map

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.utils.livedata.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MapViewModel
@ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    val restaurantData: LiveData<Resource<List<Restaurant>>> get() = _restaurantData
    private val _restaurantData = MutableLiveData<Resource<List<Restaurant>>>()

    init {
        getRestraurants()
    }

    @SuppressLint("CheckResult")
    fun getRestraurants() {
        repository.fetchRestaurants()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _restaurantData.value = Resource.success(it)

                },
                {
                    val message = it.localizedMessage ?: ""
                },
                {
                },
                {
                }
            )
    }

    fun getFromDB()= repository.getRestaurants()

    fun saveRestaurants(restaurant : Restaurant)= repository.saveRestaurants(restaurant)

}

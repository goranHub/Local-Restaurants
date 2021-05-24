package com.sicoapp.localrestaurants.ui.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicoapp.localrestaurants.data.remote.Restaurant
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.utils.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class MapViewModel
@ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    val restaurantData: LiveData<Resource<List<Restaurant>>> get() = _restaurantData
    private val _restaurantData = MutableLiveData<Resource<List<Restaurant>>>()

    init {
        getRestaurantsFromNetAndSaveIntoDB()
    }


    private fun getRestaurantsFromNetAndSaveIntoDB() {
        repository.getRestaurantsFromNetAndSaveIntoDB()
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                _restaurantData.value = Resource.success(it)
            }
    }

    fun restaurantsFormDBLiveData(){
        val data = LiveDataReactiveStreams.fromPublisher(repository.getRestaurantsDB())
    }

    fun update(restaurant: Restaurant) = repository.update(restaurant)


}

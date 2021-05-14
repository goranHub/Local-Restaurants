package com.sicoapp.localrestaurants.ui.map

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicoapp.localrestaurants.data.local.RestaurantEntity
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.domain.Restraurant
import com.sicoapp.localrestaurants.utils.livedata.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MapViewModel
@ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    val restaurantData: LiveData<Resource<List<Restraurant>>> get() = _restaurantData
    private val _restaurantData = MutableLiveData<Resource<List<Restraurant>>>()



    @SuppressLint("CheckResult")
    fun getRestaurantsFromNetAndSaveIntoDB() {
        repository.getRestaurantsFromNetAndSaveIntoDB()
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                _restaurantData.value = Resource.success(it)
            }
    }

    val restraurantsFormDBLiveData = LiveDataReactiveStreams.fromPublisher(repository.getRestaurantsDB())

    fun update(restaurant: Restraurant) = repository.update(restaurant)


}

package com.sicoapp.localrestaurants.ui.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicoapp.localrestaurants.data.local.Restaurant
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.utils.livedata.Resource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    val restaurantData: LiveData<Resource<List<Restaurant>>> get() = _restaurantData
    private val _restaurantData = MutableLiveData<Resource<List<Restaurant>>>()
    private val compositeDisposable = CompositeDisposable()



    fun getRestraurants() {
        repository.fetchRestaurants()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Restaurant>>{
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                    _restaurantData.value = Resource.loading(null)
                }

                override fun onNext(restaurant: List<Restaurant>) {
                    _restaurantData.value = Resource.success(restaurant)
                }

                override fun onError(e: Throwable) {
                    val message = e.localizedMessage ?: ""
                    _restaurantData.value = Resource.error(message, null)
                }

                override fun onComplete() {

                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

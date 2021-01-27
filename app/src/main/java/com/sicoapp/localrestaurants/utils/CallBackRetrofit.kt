package com.sicoapp.localrestaurants.utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author ll4
 * @date 1/27/2021
 */
fun<T> Call<T>.enqueueR(callback: CallBackRetrofit<T>.() -> Unit) {
    val callBackKt = CallBackRetrofit<T>()
    callback.invoke(callBackKt)
    this.enqueue(callBackKt)

}

class CallBackRetrofit<T>: Callback<T> {

    var onResponse: ((Response<T>) -> Unit)? = null
    var onFailure: ((t: Throwable?) -> Unit)? = null

    override fun onFailure(call: Call<T>, t: Throwable) {
        onFailure?.invoke(t)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        onResponse?.invoke(response)
    }

}


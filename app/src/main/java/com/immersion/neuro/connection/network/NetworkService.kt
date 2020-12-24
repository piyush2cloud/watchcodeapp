package com.immersion.neuro.connection.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NetworkService {

    companion object {
        private val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun <S> createService(serviceClass: Class<S>?): S {
            return retrofit.create(serviceClass)
        }
    }

}
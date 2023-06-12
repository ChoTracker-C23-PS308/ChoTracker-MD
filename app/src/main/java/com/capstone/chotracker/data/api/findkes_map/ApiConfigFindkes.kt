package com.capstone.chotracker.data.api.findkes_map

import androidx.viewbinding.BuildConfig
import com.capstone.chotracker.data.api.general.ApiServiceGeneral
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfigFindkes {

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

        fun getApiMapFindkes(): ApiServiceFindkes {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(ApiServiceFindkes::class.java)
        }
    }

}
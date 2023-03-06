package hr.tvz.android.qrscan.model.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ServiceRetrofit {
    fun <S> createAndGetService(serviceClass: Class<S>, ipv4: String): S {
            return Retrofit.Builder().baseUrl("http://$ipv4:8080/").addConverterFactory(GsonConverterFactory.create()).client(OkHttpClient.Builder().build()).build().create(serviceClass)
    }
}
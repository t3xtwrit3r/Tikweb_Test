package com.mubin.tikweb_test.di

import com.google.gson.Gson
import com.mubin.tikweb_test.api.ApiService
import com.mubin.tikweb_test.api.RetrofitUtils.retrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("apiService1")
    fun provideBaseUrlUnsplash() = "base url goes here"

    @Provides
    @Singleton
    fun provideRetrofitInstance(@Named("apiService1") BASE_URL: String, gson: Gson, httpClient: OkHttpClient): ApiService =
        retrofitInstance(baseUrl = BASE_URL, gson, httpClient)
            .create(ApiService::class.java)

}
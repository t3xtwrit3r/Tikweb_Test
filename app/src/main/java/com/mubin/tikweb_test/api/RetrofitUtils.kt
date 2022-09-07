package com.mubin.tikweb_test.api

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.mubin.tikweb_test.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitUtils {

    @Provides
    @Singleton
    fun getGson(): Gson {
        return GsonBuilder().setLenient().setPrettyPrinting().create()
    }

    @Provides
    @Singleton
    fun createCache(application: Application): Cache{
        val cacheSize = 5L * 1024L * 1024L //10MB
        return Cache(File(application.cacheDir, "${application.packageName}.cache"), cacheSize)
    }

    @Provides
    @Singleton
    fun createOkHttpClient(cache: Cache?): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                val logging =
                    httpLoggingInterceptor.apply {
                        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    }
                addInterceptor(logging)
            }
            //addInterceptor(NetworkConnectionInterceptor(application))
            //addInterceptor(AuthInterceptor())
            //addNetworkInterceptor(NetworkInterceptor())
            //addInterceptor(OfflineInterceptor(application))
            cache(cache)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(1, TimeUnit.MINUTES)
            connectTimeout(30, TimeUnit.SECONDS)
        }.build()
    }

    fun retrofitInstance(baseUrl: String, gson: Gson, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .baseUrl(baseUrl)
            .client(httpClient)
            .build()
    }

}
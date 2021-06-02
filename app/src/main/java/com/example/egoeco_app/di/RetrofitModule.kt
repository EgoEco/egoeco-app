package com.example.egoeco_app.di

import com.example.egoeco_app.model.EgoEcoAPIService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
//    private const val BASE_URL = "https://www.google.com"
    private const val BASE_URL = "https://api.github.com/"
    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun provideEgoEcoAPIService(retrofit: Retrofit): EgoEcoAPIService {
        return retrofit.create(EgoEcoAPIService::class.java)
    }
}
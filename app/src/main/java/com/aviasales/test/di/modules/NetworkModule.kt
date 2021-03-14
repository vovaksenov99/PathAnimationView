package com.aviasales.test.di.modules

import com.aviasales.test.data.apis.PlacesApi
import com.aviasales.test.di.qualifiers.HotellookApi
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule {

    companion object {

        private val CONVERTER_FACTORY = GsonBuilder().create()

    }

    @Singleton
    @Provides
    fun providePlacesApi(@HotellookApi retrofit: Retrofit): PlacesApi =
        retrofit.create(PlacesApi::class.java)

    @Singleton
    @Provides
    @HotellookApi
    fun providePublicRetrofit(client: OkHttpClient) =
        buildRetrofitInstance(client, "https://yasen.hotellook.com")

    @Singleton
    @Provides
    fun providePublicClient(): OkHttpClient = OkHttpClient.Builder().build()

    private fun buildRetrofitInstance(client: OkHttpClient, apiUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(apiUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(CONVERTER_FACTORY))
            .build()

}
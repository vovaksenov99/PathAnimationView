package com.aviasales.test.di

import com.aviasales.test.di.modules.NetworkModule
import com.aviasales.test.features.search.logic.SearchViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {

    fun inject(application: SearchViewModel)

}

package com.example.lieferung.di

import com.example.lieferung.Repository
import com.example.lieferung.viewmodel.ReserveViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ReserveViewModel(get()) }
}

val repositoryModule = module {
    single {
        Repository()
    }
}
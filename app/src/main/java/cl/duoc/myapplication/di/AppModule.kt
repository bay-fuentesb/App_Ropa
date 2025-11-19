package cl.duoc.myapplication.di

import cl.duoc.myapplication.database.AppDatabase
import cl.duoc.myapplication.repository.UserRepository
import cl.duoc.myapplication.viewmodel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().sessionDao() }
    single { UserRepository(get(), get()) }
    viewModel { UserViewModel(get()) }
}
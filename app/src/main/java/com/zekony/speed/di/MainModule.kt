package com.zekony.speed.di

import com.zekony.speed.data.datastore.SettingsHelper
import com.zekony.speed.ui.navigation.ThemeViewModel
import com.zekony.speed.ui.settingsScreen.viewModel.SettingsViewModel
import com.zekony.speed.ui.speedTestScreen.viewModel.SpeedTestViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object MainModule {
    val module = module {
        viewModelOf(::SpeedTestViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::ThemeViewModel)

        single {
            SettingsHelper(androidApplication())
        }
    }
}
package com.zekony.speed.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zekony.speed.data.datastore.AppThemeType
import com.zekony.speed.data.datastore.SettingsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val mSettings: SettingsHelper
) : ViewModel() {

    init {
        collectAppTheme()
    }

    private val _state = MutableStateFlow(AppThemeType.OC)
    val state = _state.asStateFlow()

    private fun collectAppTheme() {

        viewModelScope.launch(Dispatchers.Default) {
            mSettings.theme.collect { theme ->
                _state.update {
                    theme
                }
            }
        }
    }
}
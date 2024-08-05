package com.zekony.speed.ui.settingsScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zekony.speed.data.datastore.AppThemeType
import com.zekony.speed.data.datastore.SettingsHelper
import com.zekony.speed.ooklaSpeedtest.models.TestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsHelper: SettingsHelper
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        fetchTestsToRun()
        fetchCurrentTheme()
    }

    private fun fetchTestsToRun() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsHelper.tests.collect { tests ->
                _state.update {
                    it.copy(
                        testsToRun = tests
                    )
                }
            }
        }
    }

    private fun fetchCurrentTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsHelper.theme.collect { theme ->
                _state.update {
                    it.copy(
                        currentThemeType = theme
                    )
                }
            }
        }
    }

    fun checkTestType(type: TestType) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = state.value.testsToRun.toMutableSet().apply {
                if (this.contains(type)) remove(type) else add(type)
            }
            settingsHelper.saveTestsToRun(newValue)
        }
    }

    fun changeTheme(themeType: AppThemeType) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsHelper.saveAppTheme(themeType)
        }
    }
}
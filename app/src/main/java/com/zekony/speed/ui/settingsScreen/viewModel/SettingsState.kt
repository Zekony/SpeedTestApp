package com.zekony.speed.ui.settingsScreen.viewModel

import com.zekony.speed.data.datastore.AppThemeType
import com.zekony.speed.ooklaSpeedtest.models.TestType


data class SettingsState(
    val testsToRun: Set<TestType> = setOf(),
    val currentThemeType: AppThemeType? = null
)
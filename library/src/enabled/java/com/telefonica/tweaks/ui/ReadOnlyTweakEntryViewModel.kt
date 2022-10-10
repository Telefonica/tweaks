package com.telefonica.tweaks.ui

import androidx.lifecycle.ViewModel
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.TweakEntry
import com.telefonica.tweaks.domain.TweaksBusinessLogic
import kotlinx.coroutines.flow.Flow

class ReadOnlyTweakEntryViewModel<T>(
    private val tweaksBusinessLogic: TweaksBusinessLogic = Tweaks.getReference().tweaksBusinessLogic
) : ViewModel() {

    fun getValue(entry: TweakEntry): Flow<T?> = tweaksBusinessLogic.getValue(entry)
}
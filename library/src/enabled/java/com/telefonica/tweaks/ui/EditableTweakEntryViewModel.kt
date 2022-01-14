package com.telefonica.tweaks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.TweakEntry
import com.telefonica.tweaks.domain.TweaksBusinessLogic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EditableTweakEntryViewModel<T>(
    private val tweaksBusinessLogic: TweaksBusinessLogic = Tweaks.getReference().tweaksBusinessLogic
) : ViewModel() {

    fun getValue(entry: TweakEntry<T>): Flow<T?> = tweaksBusinessLogic.getValue(entry)

    fun setValue(entry: TweakEntry<T>, value: T) {
        viewModelScope.launch {
            tweaksBusinessLogic.setValue(entry, value)
        }
    }

    fun clearValue(entry: TweakEntry<T>) {
        viewModelScope.launch {
            tweaksBusinessLogic.clearValue(entry)
        }
    }
}
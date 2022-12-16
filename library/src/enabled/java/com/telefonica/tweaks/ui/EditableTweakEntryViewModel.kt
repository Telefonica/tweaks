package com.telefonica.tweaks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.Editable
import com.telefonica.tweaks.domain.TweakEntry
import com.telefonica.tweaks.domain.TweaksBusinessLogic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EditableTweakEntryViewModel<T>(
    private val tweaksBusinessLogic: TweaksBusinessLogic = Tweaks.getReference().tweaksBusinessLogic
) : ViewModel() {

    fun <T> getValue(entry: TweakEntry): Flow<T?> = tweaksBusinessLogic.getValue(entry)

    fun <T> setValue(entry: Editable<T>, value: T) {
        viewModelScope.launch {
            tweaksBusinessLogic.setValue(entry, value)
        }
    }

    fun isOverridden(entry: Editable<T>): Flow<Boolean> =
        tweaksBusinessLogic.isOverriddenOrDifferentFromDefaultValue(entry)


    fun clearValue(entry: Editable<T>) {
        viewModelScope.launch {
            tweaksBusinessLogic.clearValue(entry)
        }
    }
}
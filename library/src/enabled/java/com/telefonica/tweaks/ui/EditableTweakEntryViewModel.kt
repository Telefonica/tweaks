package com.telefonica.tweaks.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.Editable
import com.telefonica.tweaks.domain.TweakEntry
import com.telefonica.tweaks.domain.TweaksBusinessLogic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EditableTweakEntryViewModel<T>(
    private val tweakEntry: Editable<T>,
    private val tweaksBusinessLogic: TweaksBusinessLogic = Tweaks.getReference().tweaksBusinessLogic
) : ViewModel() {

    var value: T? by mutableStateOf(null)
    val entry = (tweakEntry as TweakEntry)
    init {
        viewModelScope.launch {
            tweaksBusinessLogic.getValue<T>(tweakEntry as TweakEntry).collect {
                value = it
            }
        }
    }

    fun updateValue(value: T) {
        this.value = value
        viewModelScope.launch {
            tweaksBusinessLogic.setValue(tweakEntry, value)
        }
    }

    fun isOverridden(): Flow<Boolean> =
        tweaksBusinessLogic.isOverridden(tweakEntry)

    fun clearValue() {
        this.value = null
        viewModelScope.launch {
            tweaksBusinessLogic.clearValue(tweakEntry)
        }
    }
}
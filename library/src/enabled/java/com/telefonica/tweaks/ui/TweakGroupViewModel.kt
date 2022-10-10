package com.telefonica.tweaks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.Editable
import com.telefonica.tweaks.domain.TweakGroup
import com.telefonica.tweaks.domain.TweaksBusinessLogic
import kotlinx.coroutines.launch

class TweakGroupViewModel(
    private val tweaksBusinessLogic: TweaksBusinessLogic = Tweaks.getReference().tweaksBusinessLogic,
) : ViewModel() {
    fun reset(tweakGroup: TweakGroup) {
        viewModelScope.launch {
            tweakGroup.entries
                .filterIsInstance<Editable<*>>()
                .forEach {
                    tweaksBusinessLogic.clearValue(it)
                }
        }
    }
}
package com.telefonica.tweaks.utils

import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


fun DisposableEffectScope.onStart(
    lifeCycleOwner: LifecycleOwner,
    onStart: () -> Unit
): DisposableEffectResult {
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_START) {
            onStart()
        }
    }

    lifeCycleOwner.lifecycle.addObserver(observer)

    return onDispose {
        lifeCycleOwner.lifecycle.removeObserver(observer)
    }
}

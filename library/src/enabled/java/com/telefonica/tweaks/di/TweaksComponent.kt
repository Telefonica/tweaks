package com.telefonica.tweaks.di

import com.telefonica.tweaks.Tweaks
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [TweaksModule::class]
)
internal interface TweaksComponent {
    fun inject(tweaks: Tweaks)
}
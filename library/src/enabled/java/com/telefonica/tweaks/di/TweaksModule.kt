package com.telefonica.tweaks.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class TweaksModule(private val context: Context) {

    @Provides
    fun provideContext() = context

}
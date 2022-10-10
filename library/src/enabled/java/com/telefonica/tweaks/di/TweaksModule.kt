package com.telefonica.tweaks.di

import android.content.Context
import com.telefonica.tweaks.data.TweaksRepository
import com.telefonica.tweaks.data.TweaksRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class TweaksModule(private val context: Context) {

    @Provides
    fun provideContext() = context

    @Provides
    fun provideTweaksRepository(impl: TweaksRepositoryImpl): TweaksRepository = impl

}
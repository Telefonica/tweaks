package com.telefonica.tweaks.domain

import android.util.Log
import javax.inject.Inject

class TweakTestClass @Inject constructor() {
    fun foo() {
        val a = 1
        val b = 2
        Log.d("tag","a + b = ${a + b}")
    }
}

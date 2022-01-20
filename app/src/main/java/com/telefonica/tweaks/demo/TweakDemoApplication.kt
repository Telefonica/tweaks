package com.telefonica.tweaks.demo

import android.app.Application
import android.widget.Toast
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class TweakDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Tweaks.init(this@TweakDemoApplication, demoTweakGraph())
    }

    var timestampState = flow {
        while (true) {
            emit("${System.currentTimeMillis() / 1000}")
            delay(1000)
        }
    }

private fun demoTweakGraph() = tweaksGraph {
    cover("Tweaks") {
        label("cover-key", "Current user ID:") { flowOf("80057182") }
        label("cover-ip", "Current IP:") { flowOf("192.168.1.127") }
        label("cover-ip-public", "Current IP (public):") { flowOf("80.68.1.92") }
        label("cover-ip-timestamp", "Timestamp:") { timestampState }
    }
    category("Statistics") {
        group("Group 1") {
            label(
                key = "timestamp",
                name = "Current timestamp",
            ) {
                timestampState
            }
            editableString(
                key = "value1",
                name = "Value 1",
            )
            editableBoolean(
                key = "value2",
                name = "Value 2",
                defaultValue = true,
            )
            editableLong(
                key = "value4",
                name = "Value 4",
                defaultValue = 42L,
            )
            button(
                key = "button1",
                name = "Demo button"
            ) {
                Toast.makeText(this@TweakDemoApplication, "Demo button", Toast.LENGTH_LONG)
                    .show()
            }

            routeButton(
                key = "button2",
                name = "Custom screen button",
                route = "custom-screen"
            )
        }
    }
    category("API") {}
    category("Chat") {}
    category("Crash reporting") {}
}
}
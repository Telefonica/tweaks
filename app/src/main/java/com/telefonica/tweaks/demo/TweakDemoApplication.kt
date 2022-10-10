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
        label("Current user ID:") { flowOf("80057182") }
        label("Current IP:") { flowOf("192.168.1.127") }
        label("Current IP (public):") { flowOf("80.68.1.92") }
        label("Timestamp:") { timestampState }
        dropDownMenu(
            key = "spinner1",
            name = "Spinner example",
            values = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            defaultValue = flowOf("Monday")
        )
    }
    category("Statistics") {
        group("Group 1") {
            label(
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
                name = "Demo button"
            ) {
                Toast.makeText(this@TweakDemoApplication, "Demo button", Toast.LENGTH_LONG)
                    .show()
            }
            routeButton(
                name = "Custom screen button",
                route = "custom-screen"
            )
            customNavigationButton(
                name = "Another custom screen button",
                navigation = { navController ->
                    navController.navigate("custom-screen")
                }
            )
        }
    }
    category("API") {}
    category("Chat") {}
    category("Crash reporting") {}
}
}
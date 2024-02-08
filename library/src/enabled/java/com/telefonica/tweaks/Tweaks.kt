package com.telefonica.tweaks

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.squareup.seismic.ShakeDetector
import com.telefonica.tweaks.Tweaks.Companion.TWEAKS_NAVIGATION_ENTRYPOINT
import com.telefonica.tweaks.di.DaggerTweaksComponent
import com.telefonica.tweaks.di.TweaksComponent
import com.telefonica.tweaks.di.TweaksModule
import com.telefonica.tweaks.domain.Constants.TWEAK_MAIN_SCREEN
import com.telefonica.tweaks.domain.TweakCategory
import com.telefonica.tweaks.domain.TweaksBusinessLogic
import com.telefonica.tweaks.domain.TweaksGraph
import com.telefonica.tweaks.ui.TweaksCategoryScreen
import com.telefonica.tweaks.ui.TweaksScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


open class Tweaks: TweaksContract {

    @Inject
    internal lateinit var tweaksBusinessLogic: TweaksBusinessLogic

    override fun <T> getTweakValue(key: String): Flow<T?> = tweaksBusinessLogic.getValue(key)

    override fun <T> getTweakValue(key: String, defaultValue: T): Flow<T> = getTweakValue<T>(key).map { it ?: defaultValue }

    override suspend fun <T> getTweak(key: String): T? = getTweakValue<T>(key).firstOrNull()

    override suspend fun <T> getTweak(key: String, defaultValue: T): T = getTweak(key) ?: defaultValue

    override suspend fun <T> setTweakValue(key: String, value: T) {
        tweaksBusinessLogic.setValue(key, value)
    }

    override suspend fun clearValue(key: String) {
        tweaksBusinessLogic.clearValue(key)
    }

    private fun initializeGraph(tweaksGraph: TweaksGraph) {
        tweaksBusinessLogic.initialize(tweaksGraph)
    }

    companion object {
        const val TWEAKS_NAVIGATION_ENTRYPOINT = "tweaks"
        private var reference: Tweaks = Tweaks()
        private lateinit var component: TweaksComponent

        fun init(
            context: Context,
            tweaksGraph: TweaksGraph,
        ) {
            inject(context)

            reference.initializeGraph(tweaksGraph)
        }

        @JvmStatic
        fun getReference(): Tweaks = reference

        private fun inject(context: Context) {
            component = DaggerTweaksComponent
                .builder()
                .tweaksModule(TweaksModule(context))
                .build()

            component.inject(reference)
        }
    }


}

@Composable
fun NavController.navigateToTweaksOnShake() {
    val context = LocalContext.current
    val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    LaunchedEffect(true) {
        val shakeDetector = ShakeDetector {
            vibrateIfAble(context)
            navigate(TWEAKS_NAVIGATION_ENTRYPOINT)
        }
        shakeDetector.start(sensorManager, SENSOR_DELAY_NORMAL)
    }
}

@SuppressLint("MissingPermission")
private fun vibrateIfAble(context: Context) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.VIBRATE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, 100))
            } else {
                vibrator.vibrate(200)
            }
        }
    }
}

fun NavGraphBuilder.addTweakGraph(
    navController: NavController,
    tweaksCustomTheme: @Composable (block: @Composable () -> Unit) -> Unit = {
        DefaultTweaksTheme(content = it)
    },
    customComposableScreens: NavGraphBuilder.() -> Unit = {},
) {
    val tweaksGraph = Tweaks.getReference().tweaksBusinessLogic.tweaksGraph

    val onNavigationEvent: (String) -> Unit = { route ->
        navController.navigate(route)
    }

    val onCustomNavigation: ((NavController) -> Unit) -> Unit = { navigation ->
        navigation.invoke(navController)
    }

    navigation(
        startDestination = TWEAK_MAIN_SCREEN,
        route = TWEAKS_NAVIGATION_ENTRYPOINT,
    ) {

        composable(TWEAK_MAIN_SCREEN) {
            tweaksCustomTheme {
                TweaksScreen(
                    tweaksGraph = tweaksGraph,
                    onCategoryButtonClicked = { navController.navigate(it.navigationRoute()) },
                    onNavigationEvent = onNavigationEvent,
                    onCustomNavigation = onCustomNavigation
                )
            }
        }

        tweaksGraph.categories.iterator().forEach { category ->
            composable(category.navigationRoute()) {
                tweaksCustomTheme {
                    TweaksCategoryScreen(
                        tweakCategory = category,
                        onNavigationEvent = onNavigationEvent,
                        onCustomNavigation = onCustomNavigation
                    )
                }
            }
        }
        customComposableScreens()
    }
}

private fun TweakCategory.navigationRoute(): String = "${this.title.replace(" ", "")}-tweak-screen"
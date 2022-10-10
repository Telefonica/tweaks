package com.telefonica.tweaks

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.material.MaterialTheme
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
import com.telefonica.tweaks.ui.theme.TweaksColorPalette
import com.telefonica.tweaks.ui.theme.TweaksTypography
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


open class Tweaks {

    @Inject
    internal lateinit var tweaksBusinessLogic: TweaksBusinessLogic

    open fun <T> getTweakValue(key: String): Flow<T?> = tweaksBusinessLogic.getValue(key)

    private fun initializeGraph(tweaksGraph: TweaksGraph) {
        tweaksBusinessLogic.initialize(tweaksGraph)
    }

    companion object {
        const val TWEAKS_NAVIGATION_ENTRYPOINT = "tweaks"
        private var reference: Tweaks = Tweaks()
        private lateinit var component: TweaksComponent

        fun init(
            application: Application,
            tweaksGraph: TweaksGraph,
        ) {
            inject(application)

            reference.initializeGraph(tweaksGraph)
        }

        @JvmStatic
        fun getReference(): Tweaks = reference

        private fun inject(application: Application) {
            component = DaggerTweaksComponent
                .builder()
                .tweaksModule(TweaksModule(application))
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
    tweaksCustomTheme: @Composable (block: @Composable () -> Unit) -> Unit = {
        MaterialTheme(colors = TweaksColorPalette, typography = TweaksTypography, content = it)
    },
    navController: NavController,
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

        tweaksGraph.categories.forEach { category ->
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
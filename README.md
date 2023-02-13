<p align="center">
<img src="https://img.shields.io/badge/Platform-Android-brightgreen" />
<img src="https://maven-badges.herokuapp.com/maven-central/com.telefonica/tweaks/badge.png" />
<img src="https://img.shields.io/badge/Support-%3E%3D%20Android%205.0-brightgreen" />
</p>

<p align="center">
<img src="https://user-images.githubusercontent.com/4595241/146578604-32454df7-6c15-456f-9939-7848464600e9.png" width="800" />
</p>

A customizable debug screen to view and edit flags that can be used for development in **Jetpack Compose** applications



<p align="center">
<img src="https://user-images.githubusercontent.com/4595241/194544208-8ce6cbdf-0e09-4f32-8823-b47fce7075a0.gif" data-canonical-src="https://user-images.githubusercontent.com/4595241/194544208-8ce6cbdf-0e09-4f32-8823-b47fce7075a0.gif" width="200" />
</p>


To include the library add to your app's `build.gradle`:

```gradle
implementation 'com.telefonica:tweaks:{version}'
```

Or, in case you want to don't add the library in release builds:
```gradle
debugImplementation 'com.telefonica:tweaks:{version}'
releaseImplementation 'com.telefonica:tweaks-no-op:{version}'
```

Then initialize the library in your app's `onCreate`:
```kotlin
override fun onCreate() {
    super.onCreate()
    Tweaks.init(context, demoTweakGraph())
}
```

where `demoTweakGraph` is the structure you want to be rendered:
```kotlin
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
}
```

And then, in your NavHost setup, use the extension function `NavGraphBuilder.addTweakGraph` to fill the navigation graph with the tweak components:
```kotlin
@Composable
    private fun DemoNavHost(
        navController: NavHostController,
        initialScreen: String,
        modifier: Modifier = Modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = initialScreen,
            modifier = modifier,
        ) {
            addTweakGraph(
                navController = navController,
            )
        }
    }
```

## How to build the TweaksGraph
You can use the DSL to create your own graph. Please note that a graph is composed by:
* A main group of tweaks (*Optional*)
* A list of categories

The categories are separate screens and are composed of groups of tweaks. You can use each category to separate debug elements of your app by feature or key components, for example: (chat, webviews, login, stats, etc...)

The group of tweaks are a shown inside each category screen, they are composed of tweaks and can represent configuration settings that can be grouped together, for example: endpoints of your API.

And finally, the tweaks are the configurable elements. Currently we support these ones:

```kotlin
button(
    name: String,
    action: () -> Unit
)
```
Used to display a button that performs an action

```kotlin
fun routeButton(
    name: String,
    route: String,
)
```
Similar, but this button navigates directly to a route of the NavHost, check [custom screens section](#custom-screens) for more info

```kotlin
customNavigationButton(
    name = "Another custom screen button",
    navigation = { navController ->
        navController.navigate("custom-screen") {
            popUpTo("another-custom-screen") {
                inclusive = true
            }
        }
    }
)
```
Just like `routeButton`, but it allows to pass a lambda which receives a `NavController` so more complex navigations can be performed.

```kotlin
fun label(
    name: String,
    value: () -> Flow<String>,
)
```
A non editable text

```kotlin
fun editableString(
    key: String,
    name: String,
    defaultValue: Flow<String>? = null,
)
```

```kotlin
fun editableString(
    key: String,
    name: String,
    defaultValue: String,
)
```

An editable text

```kotlin
fun editableBoolean(
    key: String,
    name: String,
    defaultValue: Flow<Boolean>? = null,
)
```

```kotlin
fun editableBoolean(
    key: String,
    name: String,
    defaultValue: Boolean,
)
```
An editable boolean 
```kotlin
fun editableInt(
    key: String,
    name: String,
    defaultValue: Flow<Int>? = null,
)
```

```kotlin
fun editableInt(
    key: String,
    name: String,
    defaultValue: Int,
) 
```
An editable Int
```kotlin
fun editableLong(
    key: String,
    name: String,
    defaultValue: Flow<Long>? = null,
)
```

```kotlin
fun editableLong(
    key: String,
    name: String,
    defaultValue: Long,
)
```
An editable Long

```kotlin
fun dropDownMenu(
    key: String,
    name: String,
    values: List<String>,
    defaultValue: Flow<String>,
)
```
A DropDownMenu
Please review the app module for configuration examples.

## Reset Button
When a group of tweaks is created, only if there is at least one editable tweak, a reset button will be automatically added.
If you do not want the reset button to be added automatically, there is a parameter in group node `withClearButton` that can be set.
```kotlin
group(
    title = "Group Title",
    withClearButton = true
) {
    // Your tweaks
}
```

## Custom screens:
You can add your custom screens to the TweaksGraph by using the `customComposableScreens` parameter of `addTweakGraph` function, for example:
```kotlin
addTweakGraph(
    navController = navController,
) {
    composable(route = "custom-screen") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Custom screen")
        }
    }
}
```

## Shake gesture support:
The tweaks can be opened when the user shakes the device, to do this you need to add to your navigation controller:
```kotlin
navController.navigateToTweaksOnShake()
```
And also, optionally
```xml
<uses-permission android:name="android.permission.VIBRATE" />
```
to your `AndroidManifest.xml`

## Special thanks to contributors:
* [Yamal Al Mahamid](https://github.com/yamal-coding)

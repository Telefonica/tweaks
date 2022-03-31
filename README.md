
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
<img src="https://user-images.githubusercontent.com/4595241/138683112-93a58d0f-1365-4392-add1-a547f4308f22.gif" data-canonical-src="https://user-images.githubusercontent.com/4595241/138683112-93a58d0f-1365-4392-add1-a547f4308f22.gif" width="200" />
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
    Tweaks.init(this@TweakDemoApplication, demoTweakGraph())
}
```

where `demoTweakGraph` is the structure you want to be rendered:
```kotlin
private fun demoTweakGraph() = tweaksGraph {
    cover("Tweaks Demo") {
        label("cover-key", "Current user ID:") { flowOf("1") }
    }
    category("Screen 1") {
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
    key: String,
    name: String,
    action: () -> Unit
)
```
Used to display a button that performs an action

```kotlin
fun routeButton(
    key: String,
    name: String,
    route: String,
)
```
Similar, but this button navigates directly to a route of the NavHost, check [custom screens section](#custom-screens) for more info



```kotlin
fun label(
    key: String,
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

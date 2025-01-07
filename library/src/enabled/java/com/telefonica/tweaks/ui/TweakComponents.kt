package com.telefonica.tweaks.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.telefonica.tweaks.TweaksTheme
import com.telefonica.tweaks.domain.ButtonTweakEntry
import com.telefonica.tweaks.domain.CustomNavigationButtonTweakEntry
import com.telefonica.tweaks.domain.DropDownMenuTweakEntry
import com.telefonica.tweaks.domain.Editable
import com.telefonica.tweaks.domain.EditableBooleanTweakEntry
import com.telefonica.tweaks.domain.EditableIntTweakEntry
import com.telefonica.tweaks.domain.EditableLongTweakEntry
import com.telefonica.tweaks.domain.EditableStringTweakEntry
import com.telefonica.tweaks.domain.ReadOnlyStringTweakEntry
import com.telefonica.tweaks.domain.RouteButtonTweakEntry
import com.telefonica.tweaks.domain.TweakCategory
import com.telefonica.tweaks.domain.TweakEntry
import com.telefonica.tweaks.domain.TweakGroup
import com.telefonica.tweaks.domain.TweaksGraph
import kotlin.math.max


@Composable
fun TweaksScreen(
    tweaksGraph: TweaksGraph,
    onCategoryButtonClicked: (TweakCategory) -> Unit,
    onNavigationEvent: (String) -> Unit,
    onCustomNavigation: ((NavController) -> Unit) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TweaksTheme.colors.tweaksBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

        tweaksGraph.cover?.let {
            TweakGroupBody(
                tweakGroup = it,
                onNavigationEvent = onNavigationEvent,
                onCustomNavigation = onCustomNavigation,
            )
        }
        tweaksGraph.categories.iterator().forEach { category ->
            TweakButton(
                onClick = { onCategoryButtonClicked(category) },
                text = category.title,
            )
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
fun TweaksCategoryScreen(
    tweakCategory: TweakCategory,
    onNavigationEvent: (String) -> Unit,
    onCustomNavigation: ((NavController) -> Unit) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TweaksTheme.colors.tweaksBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

        Text(
            text = tweakCategory.title,
            style = MaterialTheme.typography.headlineLarge,
            color = TweaksTheme.colors.tweaksOnBackground,
        )

        tweakCategory.groups.iterator().forEach { group ->
            TweakGroupBody(
                tweakGroup = group,
                onNavigationEvent = onNavigationEvent,
                onCustomNavigation = onCustomNavigation
            )
        }

        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
fun TweakGroupBody(
    tweakGroupViewModel: TweakGroupViewModel = viewModel(),
    tweakGroup: TweakGroup,
    onNavigationEvent: (String) -> Unit,
    onCustomNavigation: ((NavController) -> Unit) -> Unit,
) {
    Card(elevation = cardElevation(3.dp)) {
        Column(
            modifier = Modifier
                .background(TweaksTheme.colors.tweaksGroupBackground)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                tweakGroup.title,
                style = MaterialTheme.typography.headlineMedium,
                color = TweaksTheme.colors.tweaksOnBackground,
            )
            HorizontalDivider(thickness = 2.dp)
            tweakGroup.entries.iterator().forEach { entry ->
                when (entry) {
                    is EditableStringTweakEntry -> EditableStringTweakEntryBody(
                        EditableTweakEntryViewModel(entry)
                    )

                    is EditableBooleanTweakEntry -> EditableBooleanTweakEntryBody(
                        EditableTweakEntryViewModel(entry)
                    )

                    is EditableIntTweakEntry -> EditableIntTweakEntryBody(
                        EditableTweakEntryViewModel(entry)
                    )

                    is EditableLongTweakEntry -> EditableLongTweakEntryBody(
                        EditableTweakEntryViewModel(entry)
                    )

                    is DropDownMenuTweakEntry -> DropDownMenuTweakEntryBody(
                        items = entry.values,
                        EditableTweakEntryViewModel(entry)
                    )

                    is ReadOnlyStringTweakEntry -> ReadOnlyStringTweakEntryBody(
                        entry,
                        ReadOnlyTweakEntryViewModel()
                    )

                    is ButtonTweakEntry -> TweakButton(entry)
                    is RouteButtonTweakEntry -> TweakNavigableButton(entry, onNavigationEvent)
                    is CustomNavigationButtonTweakEntry -> TweakNavigableButton(
                        entry,
                        onCustomNavigation
                    )
                }
            }

            if (tweakGroup.entries.any { it is Editable<*> } && tweakGroup.withClearButton) {
                HorizontalDivider(thickness = 2.dp)
                ResetButton(onResetClicked = { tweakGroupViewModel.reset(tweakGroup) })
            }
        }
    }
}

@Composable
private fun ResetButton(
    onResetClicked: () -> Unit = {},
) {
    TweakButton(
        onClick = onResetClicked,
        text = "⚠️ Reset ⚠️",
    )
}

@Composable
fun TweakButton(entry: ButtonTweakEntry) {
    TweakButton(
        onClick = entry.action,
        text = entry.name,
    )
}

@Composable
fun TweakNavigableButton(
    entry: RouteButtonTweakEntry,
    onClick: (String) -> Unit,
) {
    TweakButton(
        onClick = { onClick(entry.route) },
        text = entry.name,
    )
}

@Composable
fun TweakNavigableButton(
    entry: CustomNavigationButtonTweakEntry,
    customNavigation: ((NavController) -> Unit) -> Unit,
) {
    TweakButton(
        onClick = { customNavigation(entry.navigation) },
        text = entry.name,
    )
}

@Composable
fun ReadOnlyStringTweakEntryBody(
    entry: ReadOnlyStringTweakEntry,
    tweakRowViewModel: ReadOnlyTweakEntryViewModel<String> = ReadOnlyTweakEntryViewModel(),
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val value by remember {
        tweakRowViewModel.getValue(entry)
    }.collectAsState(initial = null)
    TweakRow(
        tweakEntry = entry,
        onClick = {
            Toast
                .makeText(context, "${entry.name} = $value", Toast.LENGTH_LONG)
                .show()
        },
        onLongClick = {
            clipboardManager.setText(AnnotatedString(value.orEmpty()))
            Toast
                .makeText(context, "'$value' copied to clipboard", Toast.LENGTH_LONG)
                .show()
        }) {
        Text(
            text = "$value",
            fontFamily = FontFamily.Monospace,
            color = TweaksTheme.colors.tweaksOnBackground,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun EditableStringTweakEntryBody(
    tweakRowViewModel: EditableTweakEntryViewModel<String>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isOverridden by remember { tweakRowViewModel.isOverridden() }.collectAsState(initial = false)

    TweakRowWithEditableTextField(
        value = tweakRowViewModel.value,
        tweakRowViewModel = tweakRowViewModel,
        keyboardController = keyboardController,
        onTextFieldValueChanged = { tweakRowViewModel.updateValue(it) },
        shouldShowOverriddenLabel = isOverridden
    )
}

@Composable
fun EditableBooleanTweakEntryBody(
    tweakRowViewModel: EditableTweakEntryViewModel<Boolean>,
) {
    val context = LocalContext.current

    val isOverridden by remember { tweakRowViewModel.isOverridden() }.collectAsState(initial = false)

    TweakRow(
        tweakEntry = tweakRowViewModel.entry,
        onClick = {
            Toast
                .makeText(context, "Current value is ${tweakRowViewModel.value}", Toast.LENGTH_LONG)
                .show()
        },
        shouldShowOverriddenLabel = isOverridden
    ) {
        Checkbox(
            modifier = Modifier.size(48.dp),
            checked = tweakRowViewModel.value ?: false,
            onCheckedChange = {
                tweakRowViewModel.updateValue(it)
            },
            colors = tweaksCheckboxColors(),
        )

    }
}

@Composable
fun EditableIntTweakEntryBody(
    tweakRowViewModel: EditableTweakEntryViewModel<Int>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isOverridden by remember { tweakRowViewModel.isOverridden() }.collectAsState(initial = false)

    TweakRowWithEditableTextField(
        value = tweakRowViewModel.value,
        tweakRowViewModel = tweakRowViewModel,
        keyboardController = keyboardController,
        onTextFieldValueChanged = {
            val newValue = it.toIntOrNull() ?: 0
            tweakRowViewModel.updateValue(newValue)
        },
        shouldShowOverriddenLabel = isOverridden
    )
}

@Composable
fun EditableLongTweakEntryBody(
    tweakRowViewModel: EditableTweakEntryViewModel<Long>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isOverridden by remember { tweakRowViewModel.isOverridden() }.collectAsState(initial = false)

    TweakRowWithEditableTextField(
        value = tweakRowViewModel.value,
        tweakRowViewModel = tweakRowViewModel,
        keyboardController = keyboardController,
        onTextFieldValueChanged = {
            val newValue = it.toLongOrNull() ?: 0
            tweakRowViewModel.updateValue(newValue)
        },
        shouldShowOverriddenLabel = isOverridden
    )
}

@Composable
fun DropDownMenuTweakEntryBody(
    items: List<String>,
    tweakRowViewModel: EditableTweakEntryViewModel<String>,
) {
    var expanded by remember { mutableStateOf(false) }

    var selectedIndex by remember {
        mutableIntStateOf(max(items.indexOf(tweakRowViewModel.value), 0))
    }

    val isOverridden by remember { tweakRowViewModel.isOverridden() }.collectAsState(initial = false)

    TweakRow(
        tweakEntry = tweakRowViewModel.entry,
        onClick = {
            expanded = true
        },
        onLongClick = {
            expanded = true
        },
        shouldShowOverriddenLabel = isOverridden,
    ) {
        Text(
            text = tweakRowViewModel.value ?: "",
            fontFamily = FontFamily.Monospace,
            color = TweaksTheme.colors.tweaksOnBackground,
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .fillMaxWidth()
            .background(color = TweaksTheme.colors.tweaksDropdownItemBackground)
    ) {
        items.forEachIndexed { index, value ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = value,
                        color = TweaksTheme.colors.tweaksOnSurface
                    )
                },
                onClick = {
                    selectedIndex = index
                    expanded = false
                    tweakRowViewModel.updateValue(items[selectedIndex])
                }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TweakRow(
    tweakEntry: TweakEntry,
    onClick: (() -> Unit),
    onLongClick: (() -> Unit)? = null,
    shouldShowOverriddenLabel: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        TweakNameText(entry = tweakEntry, shouldShowOverriddenLabel = shouldShowOverriddenLabel)
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        content()
    }
}

@Composable
private fun <T> TweakRowWithEditableTextField(
    value: T?,
    tweakRowViewModel: EditableTweakEntryViewModel<T>,
    keyboardController: SoftwareKeyboardController?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onTextFieldValueChanged: (String) -> Unit,
    shouldShowOverriddenLabel: Boolean = false,
) {
    val context = LocalContext.current
    var inEditionMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    if (inEditionMode) {
        Row {
            TextField(
                modifier = Modifier
                    .weight(100F, true)
                    .focusRequester(focusRequester),
                value = if (value == null) "" else "$value",
                onValueChange = onTextFieldValueChanged,
                maxLines = 1,
                label = { Text(tweakRowViewModel.entry.name) },
                keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    inEditionMode = false
                    keyboardController?.hide()
                }),
                colors = tweaksTextFieldColors(),
            )
            IconButton(onClick = {
                tweakRowViewModel.clearValue()
                inEditionMode = false
                keyboardController?.hide()
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete",
                    tint = TweaksTheme.colors.tweaksOnBackground
                )
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else {
        TweakRow(
            tweakEntry = tweakRowViewModel.entry,
            onClick = {
                Toast
                    .makeText(context, "Current value is $value", Toast.LENGTH_LONG)
                    .show()
            },
            onLongClick = {
                inEditionMode = true
            },
            shouldShowOverriddenLabel = shouldShowOverriddenLabel
        ) {
            Text(
                text = if (value == null) "<not defined>" else "$value",
                fontFamily = FontFamily.Monospace,
                color = TweaksTheme.colors.tweaksOnBackground,
            )
        }
    }
}

@Composable
private fun TweakNameText(
    entry: TweakEntry,
    shouldShowOverriddenLabel: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = entry.name,
            style = MaterialTheme.typography.headlineSmall,
            color = TweaksTheme.colors.tweaksOnBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f, false)
        )
        if (shouldShowOverriddenLabel) {
            Text(
                " (Modified)",
                style = MaterialTheme.typography.labelMedium,
                color = TweaksTheme.colors.tweaksColorModified,
            )
        }
    }
}

@Composable
private fun tweaksButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = TweaksTheme.colors.tweaksPrimary,
    contentColor = contentColorFor(backgroundColor = TweaksTheme.colors.tweaksBackground),
    disabledContainerColor = TweaksTheme.colors.tweaksOnSurface.copy(alpha = 0.12f)
        .compositeOver(TweaksTheme.colors.tweaksSurface),
    disabledContentColor = TweaksTheme.colors.tweaksOnSurface.copy(alpha = 0.38f)
        .compositeOver(TweaksTheme.colors.tweaksSurface),
)

@Composable
private fun tweaksCheckboxColors(): CheckboxColors = CheckboxDefaults.colors(
    checkedColor = TweaksTheme.colors.tweaksPrimary,
    checkmarkColor = TweaksTheme.colors.tweaksOnPrimary,
    uncheckedColor = TweaksTheme.colors.tweaksPrimary,
)

@Composable
private fun tweaksTextFieldColors(): TextFieldColors =
    TextFieldDefaults.colors(
        focusedTextColor = TweaksTheme.colors.tweaksOnBackground,
        disabledTextColor = TweaksTheme.colors.tweaksOnBackground.copy(alpha = 0.8F),
        cursorColor = TweaksTheme.colors.tweaksPrimary,
        focusedLabelColor = TweaksTheme.colors.tweaksPrimary,
        focusedIndicatorColor = TweaksTheme.colors.tweaksPrimary,
        unfocusedIndicatorColor = TweaksTheme.colors.tweaksPrimary,
        unfocusedLabelColor = TweaksTheme.colors.tweaksPrimary,
        disabledLabelColor = TweaksTheme.colors.tweaksPrimary
    )

@Composable
internal fun TweakButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = tweaksButtonColors()
    ) {
        Text(
            text = text,
            color = TweaksTheme.colors.tweaksOnPrimary,
        )
    }
}
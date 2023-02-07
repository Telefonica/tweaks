package com.telefonica.tweaks.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            tweakCategory.title,
            style = MaterialTheme.typography.h4,
            color = TweaksTheme.colors.tweaksOnBackground,
        )

        tweakCategory.groups.iterator().forEach { group ->
            TweakGroupBody(
                tweakGroup = group,
                onNavigationEvent = onNavigationEvent,
                onCustomNavigation = onCustomNavigation
            )
        }
    }
}

@Composable
fun TweakGroupBody(
    tweakGroupViewModel: TweakGroupViewModel = viewModel(),
    tweakGroup: TweakGroup,
    onNavigationEvent: (String) -> Unit,
    onCustomNavigation: ((NavController) -> Unit) -> Unit,
) {
    Card(
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .background(TweaksTheme.colors.tweaksGroupBackground)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                tweakGroup.title,
                style = MaterialTheme.typography.h5,
                color = TweaksTheme.colors.tweaksOnBackground,
            )
            Divider(thickness = 2.dp)
            tweakGroup.entries.iterator().forEach { entry ->
                when (entry) {
                    is EditableStringTweakEntry -> EditableStringTweakEntryBody(entry,
                        EditableTweakEntryViewModel())
                    is EditableBooleanTweakEntry -> EditableBooleanTweakEntryBody(entry,
                        EditableTweakEntryViewModel())
                    is EditableIntTweakEntry -> EditableIntTweakEntryBody(entry,
                        EditableTweakEntryViewModel())
                    is EditableLongTweakEntry -> EditableLongTweakEntryBody(entry,
                        EditableTweakEntryViewModel())
                    is DropDownMenuTweakEntry -> DropDownMenuTweakEntryBody(entry,
                        EditableTweakEntryViewModel())
                    is ReadOnlyStringTweakEntry -> ReadOnlyStringTweakEntryBody(entry,
                        ReadOnlyTweakEntryViewModel())
                    is ButtonTweakEntry -> TweakButton(entry)
                    is RouteButtonTweakEntry -> TweakNavigableButton(entry, onNavigationEvent)
                    is CustomNavigationButtonTweakEntry -> TweakNavigableButton(entry,
                        onCustomNavigation)
                }
            }

            if (tweakGroup.entries.any { it is Editable<*> } && !tweakGroup.hideResetButton) {
                Divider(thickness = 2.dp)
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
            color = TweaksTheme.colors.tweaksOnBackground
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditableStringTweakEntryBody(
    entry: EditableStringTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<String> = EditableTweakEntryViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val value: String? by remember {
        tweakRowViewModel.getValue<String>(entry)
    }.collectAsState(initial = null)

    val isOverridden by remember { tweakRowViewModel.isOverridden(entry) }.collectAsState(initial = false)

    TweakRowWithEditableTextField(
        entry = entry,
        value = value,
        tweakRowViewModel = tweakRowViewModel,
        keyboardController = keyboardController,
        onTextFieldValueChanged = { tweakRowViewModel.setValue(entry, it) },
        shouldShowOverriddenLabel = isOverridden
    )
}

@Composable
fun EditableBooleanTweakEntryBody(
    entry: EditableBooleanTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<Boolean> = EditableTweakEntryViewModel(),
) {
    val context = LocalContext.current
    val value: Boolean? by remember {
        tweakRowViewModel.getValue<Boolean>(entry)
    }.collectAsState(initial = false)

    val isOverridden by remember { tweakRowViewModel.isOverridden(entry) }.collectAsState(initial = false)

    TweakRow(
        tweakEntry = entry,
        onClick = {
            Toast
                .makeText(context, "Current value is $entry.", Toast.LENGTH_LONG)
                .show()
        },
        shouldShowOverriddenLabel = isOverridden) {
        Checkbox(
            checked = value ?: false,
            onCheckedChange = {
                tweakRowViewModel.setValue(entry, it)
            },
            colors = tweaksCheckboxColors(),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditableIntTweakEntryBody(
    entry: EditableIntTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<Int> = EditableTweakEntryViewModel(),
) {
    val isOverridden by remember { tweakRowViewModel.isOverridden(entry) }.collectAsState(initial = false)

    val keyboardController = LocalSoftwareKeyboardController.current
    val value: Int? by remember {
        tweakRowViewModel.getValue<Int>(entry)
    }.collectAsState(initial = null)

    TweakRowWithEditableTextField(
        entry = entry,
        value = value,
        tweakRowViewModel = tweakRowViewModel,
        keyboardController = keyboardController,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onTextFieldValueChanged = {
            val newValue = it.toIntOrNull() ?: 0
            tweakRowViewModel.setValue(entry, newValue)
        },
        shouldShowOverriddenLabel = isOverridden,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditableLongTweakEntryBody(
    entry: EditableLongTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<Long> = EditableTweakEntryViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val value: Long? by remember {
        tweakRowViewModel.getValue<Long>(entry)
    }.collectAsState(initial = null)

    val isOverridden by remember { tweakRowViewModel.isOverridden(entry) }.collectAsState(initial = false)

    TweakRowWithEditableTextField(
        entry = entry,
        value = value,
        tweakRowViewModel = tweakRowViewModel,
        keyboardController = keyboardController,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onTextFieldValueChanged = {
            val newValue = it.toLongOrNull() ?: 0
            tweakRowViewModel.setValue(entry, newValue)
        },
        shouldShowOverriddenLabel = isOverridden,
    )
}

@Composable
fun DropDownMenuTweakEntryBody(
    entry: DropDownMenuTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<String> = EditableTweakEntryViewModel(),
) {
    val value: String? by remember {
        tweakRowViewModel.getValue<String>(entry)
    }.collectAsState(initial = null)

    var expanded by remember { mutableStateOf(false) }
    val items = entry.values
    var selectedIndex by remember {
        mutableStateOf(max(items.indexOf(value), 0))
    }

    val isOverridden by remember { tweakRowViewModel.isOverridden(entry) }.collectAsState(initial = false)

    TweakRow(
        tweakEntry = entry,
        onClick = {
            expanded = true
        },
        onLongClick = {
            expanded = true
        },
        shouldShowOverriddenLabel = isOverridden,
    ) {
        Text(
            text = "$value",
            fontFamily = FontFamily.Monospace,
            color = TweaksTheme.colors.tweaksOnBackground,
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items.forEachIndexed { index, value ->
            DropdownMenuItem(onClick = {
                selectedIndex = index
                expanded = false
                tweakRowViewModel.setValue(entry, items[selectedIndex])
            }) {
                Text(
                    text = value,
                    color = TweaksTheme.colors.tweaksOnSurface
                )
            }
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
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        TweakNameText(entry = tweakEntry, shouldShowOverriddenLabel = shouldShowOverriddenLabel)
        content()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun <T> TweakRowWithEditableTextField(
    entry: TweakEntry,
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
                label = { Text(entry.name) },
                keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    inEditionMode = false
                    keyboardController?.hide()
                }),
                colors = tweaksTextFieldColors(),
            )
            IconButton(onClick = {
                tweakRowViewModel.clearValue(entry as Editable<T>)
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
            tweakEntry = entry,
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
    Row {
        Text(text = entry.name,
            style = MaterialTheme.typography.body1,
            color = TweaksTheme.colors.tweaksOnBackground)
        if (shouldShowOverriddenLabel) {
            Text("(Modified)",
                style = MaterialTheme.typography.caption,
                color = TweaksTheme.colors.tweaksColorModified)
        }
    }
}

@Composable
private fun tweaksButtonColors(): ButtonColors = buttonColors(
    backgroundColor = TweaksTheme.colors.tweaksPrimary,
    contentColor = contentColorFor(backgroundColor = TweaksTheme.colors.tweaksBackground),
    disabledBackgroundColor = TweaksTheme.colors.tweaksOnSurface.copy(alpha = 0.12f)
        .compositeOver(TweaksTheme.colors.tweaksSurface),
    disabledContentColor = TweaksTheme.colors.tweaksOnSurface
        .copy(alpha = ContentAlpha.disabled),
)

@Composable
private fun tweaksCheckboxColors(): CheckboxColors = CheckboxDefaults.colors(
    checkedColor = TweaksTheme.colors.tweaksPrimary,
    checkmarkColor = TweaksTheme.colors.tweaksOnPrimary,
    uncheckedColor = TweaksTheme.colors.tweaksPrimary,
)

@Composable
private fun tweaksTextFieldColors(): TextFieldColors =
    TextFieldDefaults.textFieldColors(
        textColor = TweaksTheme.colors.tweaksOnBackground,
        disabledTextColor = TweaksTheme.colors.tweaksOnBackground.copy(alpha = 0.8F),
        cursorColor = TweaksTheme.colors.tweaksPrimary,
        focusedLabelColor = TweaksTheme.colors.tweaksPrimary,
        focusedIndicatorColor = TweaksTheme.colors.tweaksPrimary,
        unfocusedIndicatorColor = TweaksTheme.colors.tweaksPrimary,
        unfocusedLabelColor = TweaksTheme.colors.tweaksPrimary,
        disabledLabelColor = TweaksTheme.colors.tweaksPrimary,
    )

@Preview
@Composable
fun StringTweakEntryPreview() {
    EditableStringTweakEntryBody(
        EditableStringTweakEntry(
            key = "key",
            name = "Example",
        )
    )
}

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
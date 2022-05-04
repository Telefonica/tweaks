package com.telefonica.tweaks.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.navigation.NavController
import com.telefonica.tweaks.domain.ButtonTweakEntry
import com.telefonica.tweaks.domain.CustomNavigationButtonTweakEntry
import com.telefonica.tweaks.domain.DropDownMenuTweakEntry
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
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        tweaksGraph.cover?.let {
            TweakGroupBody(
                tweakGroup = it,
                onNavigationEvent = onNavigationEvent,
                onCustomNavigation = onCustomNavigation
            )
        }
        tweaksGraph.categories.forEach { category ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onCategoryButtonClicked(category) }) {
                Text(category.title)
            }
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
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(tweakCategory.title, style = MaterialTheme.typography.h4)

        tweakCategory.groups.forEach { group ->
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
    tweakGroup: TweakGroup,
    onNavigationEvent: (String) -> Unit,
    onCustomNavigation: ((NavController) -> Unit) -> Unit,
) {
    Card(
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(tweakGroup.title, style = MaterialTheme.typography.h5)
            Divider(thickness = 2.dp)
            tweakGroup.entries.forEach { entry ->
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
        }
    }
}

@Composable
fun TweakButton(entry: ButtonTweakEntry) {
    Button(
        onClick = entry.action,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(entry.name)
    }
}

@Composable
fun TweakNavigableButton(
    entry: RouteButtonTweakEntry,
    onClick: (String) -> Unit,
) {
    Button(
        onClick = { onClick(entry.route) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(entry.name)
    }
}

@Composable
fun TweakNavigableButton(
    entry: CustomNavigationButtonTweakEntry,
    customNavigation: ((NavController) -> Unit) -> Unit,
) {
    Button(
        onClick = { customNavigation(entry.navigation) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(entry.name)
    }
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
                .makeText(context, "${entry.key} = $value", Toast.LENGTH_LONG)
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
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EditableStringTweakEntryBody(
    entry: EditableStringTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<String> = EditableTweakEntryViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val value: String? by remember {
        tweakRowViewModel.getValue(entry)
    }.collectAsState(initial = null)

    TweakRowWithEditableTextField(
        entry,
        context,
        value,
        tweakRowViewModel,
        keyboardController,
        onTextFieldValueChanged = { tweakRowViewModel.setValue(entry, it) }
    )
}

@Composable
fun EditableBooleanTweakEntryBody(
    entry: EditableBooleanTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<Boolean> = EditableTweakEntryViewModel(),
) {
    val context = LocalContext.current
    val value: Boolean? by remember {
        tweakRowViewModel.getValue(entry)
    }.collectAsState(initial = false)

    TweakRow(
        tweakEntry = entry,
        onClick = {
            Toast
                .makeText(context, "Current value is $entry.", Toast.LENGTH_LONG)
                .show()
        }) {
        Checkbox(checked = value ?: false, onCheckedChange = {
            tweakRowViewModel.setValue(entry, it)
        })
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EditableIntTweakEntryBody(
    entry: EditableIntTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<Int> = EditableTweakEntryViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val value: Int? by remember {
        tweakRowViewModel.getValue(entry)
    }.collectAsState(initial = null)

    TweakRowWithEditableTextField(
        entry,
        context,
        value,
        tweakRowViewModel,
        keyboardController,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onTextFieldValueChanged = {
            val newValue = it.toIntOrNull() ?: 0
            tweakRowViewModel.setValue(entry, newValue)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EditableLongTweakEntryBody(
    entry: EditableLongTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<Long> = EditableTweakEntryViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val value: Long? by remember {
        tweakRowViewModel.getValue(entry)
    }.collectAsState(initial = null)

    TweakRowWithEditableTextField(
        entry,
        context,
        value,
        tweakRowViewModel,
        keyboardController,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onTextFieldValueChanged = {
            val newValue = it.toLongOrNull() ?: 0
            tweakRowViewModel.setValue(entry, newValue)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DropDownMenuTweakEntryBody(
    entry: DropDownMenuTweakEntry,
    tweakRowViewModel: EditableTweakEntryViewModel<String> = EditableTweakEntryViewModel(),
) {
    val value: String? by remember {
        tweakRowViewModel.getValue(entry)
    }.collectAsState(initial = null)

    var expanded by remember { mutableStateOf(false) }
    val items = entry.values
    var selectedIndex by remember {
        mutableStateOf(max(items.indexOf(value), 0))
    }

    TweakRow(
        tweakEntry = entry,
        onClick = {
            expanded = true
        },
        onLongClick = {
            expanded = true
        }
    ) {
        Text(
            text = "$value",
            fontFamily = FontFamily.Monospace,
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
                val currentValue = value
                tweakRowViewModel.setValue(entry, items[selectedIndex])
            }) {
                Text(text = value)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TweakRow(
    tweakEntry: TweakEntry<*>,
    onClick: (() -> Unit),
    onLongClick: (() -> Unit)? = null,
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
        TweakNameText(entry = tweakEntry)
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun <T> TweakRowWithEditableTextField(
    entry: TweakEntry<T>,
    context: Context,
    value: T?,
    tweakRowViewModel: EditableTweakEntryViewModel<T>,
    keyboardController: SoftwareKeyboardController?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onTextFieldValueChanged: (String) -> Unit,
) {

    var inEditionMode by remember { mutableStateOf(false) }

    TweakRow(
        tweakEntry = entry,
        onClick = {
            Toast
                .makeText(context, "Current value is $value", Toast.LENGTH_LONG)
                .show()
        },
        onLongClick = {
            inEditionMode = true
        }) {

        if (inEditionMode) {
            TextField(
                modifier = Modifier.weight(100F, true),
                value = "$value",
                onValueChange = onTextFieldValueChanged,
                maxLines = 1,
                keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    inEditionMode = false
                    keyboardController?.hide()
                }),
            )
            IconButton(onClick = {
                tweakRowViewModel.clearValue(entry)
                inEditionMode = false
                keyboardController?.hide()
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
            }
        } else {
            Text(
                text = "$value",
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun TweakNameText(entry: TweakEntry<*>) {
    Text(text = entry.name, style = MaterialTheme.typography.body1)
}

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
package com.mahikr.keepup.ui.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TextFieldComponent(
    content: String,
    onContentChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
        .fillMaxWidth(0.75f)
        .clip(MaterialTheme.shapes.medium),
    keyboardOptions:KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    isSingleLined:Boolean = true
) {
    TextField(
        value = content,
        onValueChange = onContentChange,
        label = { /*Text*/PlainTextComponent(text = label) },
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        singleLine = isSingleLined
    )
}
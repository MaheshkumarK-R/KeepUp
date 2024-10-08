package com.mahikr.keepup.ui.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit

@Composable
fun PlainTextComponent(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    style: TextStyle = TextStyle.Default,
    textAlign: TextAlign = TextAlign.Center,
    fontSize: TextUnit = TextStyle.Default.fontSize,
    color:Color = TextStyle.Default.color
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        textAlign = textAlign,
        fontWeight = fontWeight,
        fontSize = fontSize,
        color = color
    )
}
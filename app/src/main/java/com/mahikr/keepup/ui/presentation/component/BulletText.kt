package com.mahikr.keepup.ui.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BulletText(content: String) {
    val textColour = MaterialTheme.colorScheme.onSurface
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
        withStyle(style = paragraphStyle) {
            withStyle(
                style = SpanStyle(
                    color = textColour
                )
            ) {
                append(Typography.bullet)
                append("\t\t")
            }
            withStyle(
                style = SpanStyle(
                    color = textColour
                )
            ) {
                append(content)
            }
        }
    }
    Text(text = annotatedString, modifier = Modifier.padding(horizontal = 25.dp))
}

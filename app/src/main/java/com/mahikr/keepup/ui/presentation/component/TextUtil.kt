package com.mahikr.keepup.ui.presentation.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

fun buildAnnotatedString(
    textColour: Color, tag: String, annotation: String, content: String
): AnnotatedString {
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    return androidx.compose.ui.text.buildAnnotatedString {
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
            append(" ")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFB5BCDD), textDecoration = TextDecoration.Underline
                )
            ) {
                pushStringAnnotation(tag = tag, annotation = annotation)
                append(tag)
            }
        }
    }
}

package com.mahikr.keepup.ui.presentation.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mahikr.keepup.R

private const val TAG = "HyperLinkText_TAG"
@Composable
fun HyperLinkText(
    leetCodeQuestions: List<String>,
    title: String = "LeetCode Questions: ",
    onClick: (String) -> Unit,
    painterId: Int? = R.drawable.leetcode
) {
    SideEffect {
        Log.d(TAG, "HyperLinkText: $leetCodeQuestions")
    }
    val textColour = MaterialTheme.colorScheme.onSurface
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            painterId?.let {
                Image(
                    painter = painterResource(id = painterId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            PlainTextComponent(
                text = title, fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
        leetCodeQuestions.forEach {
            val strings = it.split(": ")
            Log.d(TAG, "HyperLinkText: $it\nsplit strings $strings")
            val annotatedString = buildAnnotatedString(
                textColour, "link", annotation = strings[1], content = strings[0]
            )
            ClickableText(text = annotatedString, onClick = { offset ->
                annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let { span ->
                    Log.d("_TAG", "MainContent: Clicked on ${span.item}")
                    onClick(span.item)
                }
            }, modifier = Modifier.padding(horizontal = 25.dp))
        }
    }
}

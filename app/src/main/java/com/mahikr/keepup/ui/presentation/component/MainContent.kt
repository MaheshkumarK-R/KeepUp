package com.mahikr.keepup.ui.presentation.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mahikr.keepup.R
import com.mahikr.keepup.ui.presentation.screen.Topic

@Composable
fun MainContent(
    dayIndex: Long,
    title: String,
    leetCodeQuestions: List<String>,
    systemDesignTopic: String,
    communicationExercise: String,
    userName: String
) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.days_images),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            PlainTextComponent(
                text = "Hey: $userName, Welcome to The",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = Color(0x85D36A6A),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(
                    Alignment.TopCenter
                )
            )
            PlainTextComponent(
                text = "Day: $dayIndex  $title",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(
                    Alignment.BottomStart
                ),
                color = Color.Red.copy(alpha = 0.5f)
            )
        }

        val handlerUrl = LocalUriHandler.current

        HyperLinkText(leetCodeQuestions = leetCodeQuestions, onClick = {
            Log.d("_TAG", "MainContent: Clicked on $it")
            handlerUrl.openUri(it)
        })

        Topic(R.drawable.system_integration, "System Design Topic: ")
        BulletText(systemDesignTopic)
        Topic(R.drawable.taskdone, "Communication Exercise: ")
        BulletText(communicationExercise)
    }
}
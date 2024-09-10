package com.mahikr.keepup.ui.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mahikr.keepup.R
import com.mahikr.keepup.ui.presentation.component.PlainTextComponent


@Composable
fun AccomplishScreen(onPreviousTask: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.taskdone),
                alpha = 0.135f, contentScale = ContentScale.FillBounds
            )
            .padding(vertical = 20.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PlainTextComponent(
                    text = stringResource(R.string.congrats_string),
                    color = Color(0xFF74BEC1),
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
            Box(modifier = Modifier.padding(vertical = 20.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.taskdone),
                    contentDescription = null
                )
                PlainTextComponent(
                    text = stringResource(R.string.congrats_1_string),
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    color = Color(0xFFFF5B7E),
                    modifier = Modifier.align(Alignment.BottomCenter),
                    fontWeight = FontWeight.ExtraBold
                )
            }
            PlainTextComponent(
                text = stringResource(R.string.additional_resources_string),
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.SemiBold
            )

            HyperLinkText(
                leetCodeQuestions = listOf("LeetCode: https://leetcode.com/"),
                title = stringResource(R.string.leetcode_string),
                onClick = {
                    uriHandler.openUri(it)
                })
            HyperLinkText(
                leetCodeQuestions = listOf("System Design Primer: https://github.com/donnemartin/system-design-primer"),
                title = stringResource(R.string.system_design_primer_string),
                onClick = {
                    uriHandler.openUri(it)
                })
            HyperLinkText(
                leetCodeQuestions = listOf("Coding Interview University: https://github.com/jwasham/coding-interview-university"),
                title = stringResource(R.string.coding_interview_university_string),
                onClick = {
                    uriHandler.openUri(it)
                })
            HyperLinkText(
                leetCodeQuestions = listOf("Communication Skills for Tech Professionals: https://www.coursera.org/learn/communication-skills"),
                title = stringResource(R.string.communication_skills_string),
                onClick = {
                    uriHandler.openUri(it)
                })
            HyperLinkText(
                leetCodeQuestions = listOf("DSA playlist : https://www.youtube.com/watch?v=bum_19loj9A&list=PLBZBJbE_rGRV8D7XZ08LK6z-4zPoWzu5H"),
                title = stringResource(R.string.dsa_playlist_string),
                onClick = {
                    uriHandler.openUri(it)
                })
            HyperLinkText(
                leetCodeQuestions = listOf("Neetcode channel : https://www.youtube.com/@NeetCode/playlists"),
                title = stringResource(R.string.neetcode_channel_string),
                onClick = {
                    uriHandler.openUri(it)
                })
            HyperLinkText(
                leetCodeQuestions = listOf("System design interview by freecodecamp: https://youtu.be/F2FmTdLtb_4"),
                title = stringResource(R.string.system_design_interview_by_freecodecamp_string),
                onClick = {
                    uriHandler.openUri(it)
                })
            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = onPreviousTask) {
                    Text(text = stringResource(R.string.previous_task_string))
                }
            }
        }
    }
}

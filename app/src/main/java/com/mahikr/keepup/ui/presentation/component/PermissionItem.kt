package com.mahikr.keepup.ui.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mahikr.keepup.R

@Composable
fun PermissionItem(content: String, action: () -> Unit, actionText: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
                .paint(painter = painterResource(id = R.drawable.ic_launcher_background)),
            shape = CardDefaults.elevatedShape
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = content, modifier = Modifier.align(Alignment.CenterHorizontally), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = action) {
                Text(actionText)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
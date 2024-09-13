package com.mahikr.keepup.ui.presentation.component

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

data class PermissionRequestInfo(val permission: String, val featureInfo: String)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionInfo(permis: PermissionState, permissionRequestInfo: PermissionRequestInfo) {
    val context = LocalContext.current
    when {
        permis.status.isGranted -> {
            Text(text = "${permissionRequestInfo.permission} Permission Granted")
        }

        permis.status.shouldShowRationale -> {
            PermissionItem(
                content = "${permissionRequestInfo.permission} Permission\nis required to function the ${permissionRequestInfo.featureInfo}",
                action = { permis.launchPermissionRequest() },
                actionText ="Request ${permissionRequestInfo.permission} Permission"
            )
        }

        !permis.status.isGranted && !permis.status.shouldShowRationale -> {
            PermissionItem(
                content = "${permissionRequestInfo.permission} Permission Denied.\nGo To App settings for enabling",
                action = {
                    // Open app settings to manually enable the permission
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                actionText = "Open App Settings"
            )
        }
    }
}
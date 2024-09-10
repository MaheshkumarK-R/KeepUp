package com.mahikr.keepup.ui.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahikr.keepup.ui.presentation.component.LoadingComponent
import com.mahikr.keepup.ui.presentation.component.LoginContent
import com.mahikr.keepup.ui.presentation.state.LoadingComponentState
import com.mahikr.keepup.ui.presentation.state.login.LoginEvent
import com.mahikr.keepup.ui.vm.LoadingVm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onNavigate: () -> Unit, loadingVm: LoadingVm = hiltViewModel()) {

    val snackBarHostState = remember { SnackbarHostState() }
    var showSnackBar by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val state: LoadingComponentState by loadingVm.loadingState.collectAsStateWithLifecycle(
        LoadingComponentState.Loading("Synchronizing data\nPlease wait")
    )
    val existingUser by loadingVm.onNavigateFlow.collectAsStateWithLifecycle(initialValue = false)

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var snackBarMessage by remember { mutableStateOf("") }

    LaunchedEffect(showSnackBar) {
        if (showSnackBar) {
            snackBarHostState.showSnackbar(
                message = snackBarMessage,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            showSnackBar = false
        }
    }

    LaunchedEffect(state) {
        if (state == LoadingComponentState.Idle) {
            if (name != "" && phoneNumber != "") {
                name = ""
                phoneNumber = ""
                onNavigate()
            }
        }
    }

    LaunchedEffect(existingUser) {
        Log.d("TAGi", "existingUser $existingUser")
        if (existingUser) onNavigate()
        delay(500L)
        loadingVm.updateLoadingState(LoadingComponentState.Idle)
    }

    SideEffect {
        Log.d("TAGi", "existingUser $existingUser & loading state $state")
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.clip(MaterialTheme.shapes.extraSmall)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error,
                    actionColor = Color.Green.copy(alpha = 0.75f)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val uiState = state) {

                is LoadingComponentState.Idle -> {
                    LoginContent(
                        name = name,
                        onNameChange = { name = it },
                        phoneNumber = phoneNumber,
                        onPhoneNumberChange = {
                            if (it.isDigitsOnly() && it.length <= 10)
                                phoneNumber = it
                        }
                    ) {
                        if (name.isNotEmpty() && name.isNotBlank() && phoneNumber.isDigitsOnly() && phoneNumber.length == 10)
                            scope.launch(Dispatchers.Default) {
                                loadingVm.onLoginEvent(
                                    LoginEvent.OnRegister(
                                        name,
                                        phoneNumber
                                    )
                                )
                            }
                        else {
                            if (name.isNotEmpty() && name.isNotBlank()) snackBarMessage =
                                "name can't be blank"
                            if (phoneNumber.length < 10) snackBarMessage =
                                "number should be 10 digits"
                            showSnackBar = true
                        }
                    }
                }

                is LoadingComponentState.Loading -> {
                    LoadingComponent(
                        msg = uiState.message?:"Loading",
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }
        }
    }

}


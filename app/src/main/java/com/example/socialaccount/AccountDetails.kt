package com.example.socialaccount

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AccountDetails(auth: FirebaseAuth) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "- Hello User -",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.padding(40.dp))

//        Button(onClick = {
//            auth.signOut()
//        }) {
//            Text(text = "Sign Out")
//        }

        val dispatcher = LocalOnBackPressedDispatcherOwner.current
        val callback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Prevent going back
                }
            }
        }
        DisposableEffect(dispatcher) {
            dispatcher?.onBackPressedDispatcher?.addCallback(callback)
            onDispose {
                callback.remove()
            }
        }
    }
}
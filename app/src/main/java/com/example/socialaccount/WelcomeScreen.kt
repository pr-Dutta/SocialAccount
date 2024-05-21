package com.example.socialaccount

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    context: MainActivity,
    navigateToSignInScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.padding(32.dp))

        Row {
            Button(onClick = { navigateToSignInScreen() }) {
                Text(text = "Log in")
            }

            Spacer(modifier = Modifier.padding(24.dp))

            Button(onClick = { navigateToSignUpScreen() }) {
                Text(text = "Sign Up")
            }
        }

        Text(
            text = "Or",
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Login with google",
            modifier = Modifier.padding(16.dp)
        )

        Icon(
            painter = painterResource(R.drawable.google_icon),
            contentDescription = "google icon",
            Modifier
                .size(32.dp)
                .clickable {
                    //signIn()
                },
            tint = Color.White
        )

    }
}
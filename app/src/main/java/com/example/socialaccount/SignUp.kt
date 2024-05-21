package com.example.socialaccount

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUp(
    context: MainActivity,
    auth: FirebaseAuth,
    navigateToSignInScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign - Up",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(8.dp)
        )

        val userName = remember{ mutableStateOf("") }
        OutlinedTextField(
            value = userName.value,
            onValueChange = { userName.value = it },
            placeholder = { Text(text = "User name") },
            modifier = Modifier
                .padding(8.dp)
        )

        val email = remember{ mutableStateOf("") }
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = { Text(text = "Email") },
            modifier = Modifier
                .padding(8.dp)
        )

        val password = remember{ mutableStateOf("") }
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = { Text(text = "Password") },
            modifier = Modifier
                .padding(8.dp)
        )

        val passwordRe = remember{ mutableStateOf("") }
        OutlinedTextField(
            value = passwordRe.value,
            onValueChange = { passwordRe.value = it },
            placeholder = { Text(text = "Repeat - Password") },
            modifier = Modifier
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.padding(48.dp))


        Row {
            Button(onClick = { navigateToSignInScreen() }) {
                Text(text = "Sign in")
            }

            Spacer(modifier = Modifier.padding(24.dp))

            Button(onClick = {
                if (userName.value.isEmpty()
                    || email.value.isEmpty()
                    || password.value.isEmpty()
                    || passwordRe.value.isEmpty()
                ) {
                    Toast.makeText(context, "Please fill all the details", Toast.LENGTH_SHORT).show()
                }else if (password.value != passwordRe.value) {
                    Toast.makeText(context, "Repeat password must be same", Toast.LENGTH_SHORT).show()
                }else {
                    auth.createUserWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener(context) {
                                task ->

                            if (task.isSuccessful) {
                                Toast.makeText(context, "Register successful", Toast.LENGTH_SHORT).show()
                                navigateToSignInScreen()
                            }else {
                                Toast.makeText(context, "Register failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }) {
                Text(text = "Register")
            }
        }

    }
}
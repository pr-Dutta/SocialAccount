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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun SignIn(
    context: MainActivity,
    auth: FirebaseAuth,
    navigateToSignUpScreen: () -> Unit,
    navigateToAccountDetails: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign - In",
            fontSize = 24.sp,
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

        Text(
            text = "Forgot password",
            fontSize = 16.sp,
            modifier = Modifier
                .padding(8.dp)
        )


        Row {
            Button(onClick = {
                if (email.value.isEmpty() || password.value.isEmpty()) {
                    Toast.makeText(context, "Please fill all the details", Toast.LENGTH_SHORT).show()
                }else {
                    auth.signInWithEmailAndPassword(email.value, password.value)
                        .addOnCompleteListener {
                                task ->

                            if (task.isSuccessful) {
                                Toast.makeText(context, "Sign-In successfully", Toast.LENGTH_SHORT).show()
                                navigateToAccountDetails()
                                //context.finish()
                            }else {
                                Toast.makeText(context, "Sign-In failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.padding(24.dp))

            Button(onClick = {
                navigateToSignUpScreen()
            }) {
                Text(text = "Register")
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        val isResend = remember { mutableStateOf(false) }
        Text(
            text = "Or",
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Login with phone number",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val phoneNumber = remember{ mutableStateOf("") }
        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            placeholder = { Text(text = "Enter phone number") },
            modifier = Modifier
                .padding(16.dp)
        )


        var verificationCode: String
        var resendingToken: PhoneAuthProvider.ForceResendingToken

        Row {
            Button(onClick = {
                val builder = PhoneAuthOptions.newBuilder()
                    .setPhoneNumber(phoneNumber.value)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(context)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            signIn(credential)
                        }

                        private fun signIn(credential: PhoneAuthCredential) {
                            // Login and go to Profile
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Toast.makeText(context, "OTP verification failed", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken,
                        ) {
                            verificationCode = verificationId
                            resendingToken = token
                            Toast.makeText(context, "OTP send successfully", Toast.LENGTH_SHORT).show()
                        }
                    }).build()


                PhoneAuthProvider.verifyPhoneNumber(builder)

//                if (!isResend.value) {
//                    PhoneAuthProvider.verifyPhoneNumber(builder)
//                }

            }) {
                Text(text = "Send OTP")
            }

            Spacer(modifier = Modifier.padding(24.dp))

            Button(onClick = {
                navigateToSignUpScreen()
            }) {
                Text(text = "Re-send OTP")
            }
        }
    }
}
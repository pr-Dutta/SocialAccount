package com.example.socialaccount

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.socialaccount.ui.theme.SocialAccountTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val auth = FirebaseAuth.getInstance()
            var context = this

            SocialAccountTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SocialAccountApp(context, auth)
                }
            }
        }
    }
}

@Composable
fun SocialAccountApp(context: MainActivity, auth: FirebaseAuth ) {

    var startScreen: String

    val currentUser : FirebaseUser? = auth.currentUser
    if (currentUser != null) {
        startScreen = "account_details_screen"
    }else {
        startScreen = "welcome_screen"
    }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startScreen) {

        composable("welcome_screen") {
            WelcomeScreen(
                navigateToSignInScreen = { navController.navigate("sign_in_screen") },
                navigateToSignUpScreen = { navController.navigate("sign_up_screen") }
            )
        }

        composable(route = "sign_in_screen") {
            SignIn(
                context,
                auth,
                navigateToSignUpScreen = { navController.navigate("sign_up_screen") },
                navigateToAccountDetails = { navController.navigate("account_details") }
            )
        }

        composable(route = "sign_up_screen") {
            SignUp(
                context,
                auth,
            ) { navController.navigate("sign_in_screen") }
        }

        composable(route = "account_details_screen") {
            AccountDetails(auth)
        }
    }
}


@Composable
fun WelcomeScreen(
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
    }
}

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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SocialAccountTheme {

    }
}
package com.example.socialaccount

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.socialaccount.ui.theme.SocialAccountTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        var context = this

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            SocialAccountTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SocialAccountApp(context, mAuth)
                }
            }
        }
    }
}

@Composable
fun SocialAccountApp(
    context: MainActivity,
    auth: FirebaseAuth
) {

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
                context,
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




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SocialAccountTheme {

    }
}
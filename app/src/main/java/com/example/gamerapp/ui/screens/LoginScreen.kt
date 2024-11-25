package com.example.gamerapp.ui.screens



import android.app.Activity

import android.content.Intent

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp

import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.GoogleAuthProvider

import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase

import net.openid.appauth.*



@Composable

fun LoginScreen(

    onLoginSuccess: () -> Unit

) {

    val context = LocalContext.current

    val auth = Firebase.auth

    

    // Google Sign In setup

    val gso = remember {

        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

            .requestIdToken("453268896795-4731s0v8pvjhuludjf13hfgaeg7ui6uo.apps.googleusercontent.com")

            .requestEmail()

            .build()

    }

    

    val googleSignInClient = remember {

        GoogleSignIn.getClient(context, gso)

    }



    val googleSignInLauncher = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.StartActivityForResult()

    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {

                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)

                    .addOnSuccessListener { 

                        onLoginSuccess()

                    }

                    .addOnFailureListener {

                        // Handle error

                    }

            } catch (e: ApiException) {

                // Handle sign-in failure

            }

        }

    }



    Column(

        modifier = Modifier

            .fillMaxSize()

            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Text(

            text = "Welcome to Gamer App",

            style = MaterialTheme.typography.headlineMedium

        )

        

        Spacer(modifier = Modifier.height(32.dp))

        

        Button(

            onClick = {

                googleSignInLauncher.launch(googleSignInClient.signInIntent)

            },

            modifier = Modifier.fillMaxWidth(0.8f)

        ) {

            Text("Sign in with Google")

        }



        Spacer(modifier = Modifier.height(16.dp))



        Button(

            onClick = {

                val authRequest = AuthorizationRequest.Builder(

                    AuthorizationServiceConfiguration(

                        Uri.parse("https://discord.com/api/oauth2/authorize"),

                        Uri.parse("https://discord.com/api/oauth2/token")

                    ),

                    "1255052347575504927",

                    ResponseTypeValues.CODE,

                    Uri.parse("com.example.gamerapp://oauth2callback")

                ).setScopes("identify", "email")

                    .build()



                val authService = AuthorizationService(context)

                val authIntent = authService.getAuthorizationRequestIntent(authRequest)

                context.startActivity(authIntent)

                auth.signInAnonymously().addOnSuccessListener {

                    onLoginSuccess()

                }

            },

            modifier = Modifier.fillMaxWidth(0.8f)

        ) {

            Text("Sign in with Discord")

        }

    }

} 

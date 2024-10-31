package com.example.skysync

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skysync.ui.theme.Purple40
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    val name: String?, val mail: String, val imageUploaded: Boolean
)

@Composable
fun AuthenticationScreen(authentication: FirebaseAuth, context: Context) {

    var user by remember { mutableStateOf(authentication.currentUser) }

    if (user == null) {
        Login(authentication = authentication, onSignedIn = { signedUser -> user = signedUser })
    } else {
        MainScreen(
            user = user!!, context = context
        )
    }
}

@Composable
fun Login(
    authentication: FirebaseAuth, onSignedIn: (FirebaseUser) -> Unit, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var forgot by remember { mutableStateOf(false) }
    var isSignedIn by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = modifier
                    .width(280.dp)
                    .height(40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (isSignedIn) {
                    Text(
                        text = "Sign In",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                } else {
                    Text(
                        text = "Create Account",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

            }

            Spacer(modifier = modifier.height(70.dp))

            Column(
                modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = modifier.height(20.dp))

                if (!isSignedIn) {
                    OutlinedTextField(
                        value = name,
                        textStyle = TextStyle(color = Color.Black),
                        onValueChange = { name = it },
                        placeholder = { Text(text = "Name", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Name Icon",
                                tint = Color.Black
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        label = { Text(text = "Name", color = Color.Black) },
                        colors = TextFieldDefaults.colors(
                            cursorColor = Color.Black,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                        )
                    )

                    Spacer(modifier = modifier.height(20.dp))
                }

                OutlinedTextField(
                    value = userName,
                    textStyle = TextStyle(color = Color.Black),
                    onValueChange = { userName = it },
                    placeholder = { Text(text = "username@domain", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color.Black
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    label = { Text(text = "Email", color = Color.Black) },
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                    )
                )

                Spacer(modifier = modifier.height(20.dp))

                OutlinedTextField(
                    value = password,
                    label = { Text(text = "Password", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    onValueChange = { password = it },
                    placeholder = { Text(text = "Password", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = Color.Black
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            val icon =
                                if (isPasswordVisible) ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24)
                                else ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)
                            Icon(
                                imageVector = icon,
                                contentDescription = "Password Visibility",
                                tint = Color.Black
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                    )
                )

                Spacer(modifier = modifier.height(20.dp))

                if (isSignedIn) {
                    TextButton(onClick = { forgot = true }) {
                        Text(text = "Forgot Password", color = Purple40)
                    }

                    if (forgot) {
                        context.startActivity(Intent(context, ForgotPassword::class.java))
                        forgot = false
                    }
                }

                ElevatedButton(modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 0.dp)
                    .width(150.dp)
                    .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = ButtonDefaults.buttonElevation(100.dp),
                    shape = MaterialTheme.shapes.large,

                    onClick = {
                        if (isSignedIn) {
                            if (userName.isNotEmpty() && password.isNotEmpty()) {
                                authentication.signInWithEmailAndPassword(userName, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            val user = authentication.currentUser

                                            if (user != null) {
                                                onSignedIn(user)
                                                Toast.makeText(
                                                    context, "Signed in", Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                context, "User not Registered", Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    context, "Please fill the details", Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            if (userName.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                                authentication.createUserWithEmailAndPassword(userName, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            val user = authentication.currentUser
                                            val userProfile = hashMapOf(
                                                "name" to name,
                                                "email" to userName,
                                                "imageUploaded" to false
                                            )

                                            val firestore = FirebaseFirestore.getInstance()
                                            firestore.collection("users").document(user!!.uid)
                                                .set(userProfile).addOnSuccessListener {
                                                    onSignedIn(user)
                                                    Toast.makeText(
                                                        context, "Data added ", Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Unable to Create Account",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }
                    }) {
                    Text(
                        text = if (isSignedIn) "Sign in" else "Sign Up",
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                }

                if (isSignedIn) {
                    Text(
                        text = "Don't have account ?", color = Color.Black
                    )
                }

                TextButton(onClick = {
                    isSignedIn = !isSignedIn
                    name = ""
                    userName = ""
                    password = ""
                    isPasswordVisible = false
                }) {
                    Text(
                        text = if (isSignedIn) "Sign up" else "Sign in", color = Purple40
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(user: FirebaseUser, context: Context) {
    val userProfile = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(user.uid) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(user.uid)

        userDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                if (document.exists()) {
                    val name = document.getString("name")
                    val imageUploaded = document.getBoolean("imageUploaded")
                    userProfile.value = imageUploaded?.let { User(name, user.email ?: "", it) }
                }
            }
        }.addOnFailureListener {}
    }

    val intent = Intent(context, HomeScreen::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    intent.putExtra("UserName", userProfile.value?.name)
    intent.putExtra("imageUploaded", userProfile.value?.imageUploaded)
    context.startActivity(intent)
}

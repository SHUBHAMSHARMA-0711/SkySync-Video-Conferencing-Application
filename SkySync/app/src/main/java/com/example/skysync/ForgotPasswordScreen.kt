package com.example.skysync

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skysync.ui.theme.SkySyncTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ForgotPassword : ComponentActivity() {

    private var authentication: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        authentication = FirebaseAuth.getInstance()

        setContent {
            SkySyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ForgetPassword(authentication)
                }
            }
        }
    }
}

@Composable
@Suppress("DEPRECATION")
fun ForgetPassword(auth: FirebaseAuth, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    var emailAddress by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop
        )

        Column {
            Spacer(modifier = modifier.height(40.dp))

            val activity = context as? Activity
            IconButton(modifier = Modifier.padding(8.dp), onClick = {
                activity?.onBackPressed()
            }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back Arrow Button",
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = modifier
                    .width(280.dp)
                    .height(40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Forgot Password",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = modifier.height(50.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = modifier.height(20.dp))

                OutlinedTextField(
                    value = emailAddress,
                    textStyle = TextStyle(color = Color.Black),
                    onValueChange = { emailAddress = it },
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

                Spacer(modifier = modifier.height(50.dp))

                ElevatedButton(
                    onClick = {
                        if (emailAddress.isNotEmpty() && emailAddress.contains("@") && emailAddress.contains(
                                "."
                            )
                        ) {
                            auth.sendPasswordResetEmail(emailAddress).addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Mail have been sent to $emailAddress",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context, "Please Enter your Email Address", Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 0.dp)
                        .width(150.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = ButtonDefaults.buttonElevation(100.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(text = "Verify")
                }
            }
        }
    }
}

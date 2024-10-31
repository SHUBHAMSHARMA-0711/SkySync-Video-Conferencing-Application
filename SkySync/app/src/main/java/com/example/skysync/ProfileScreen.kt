package com.example.skysync

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileScreen : AppCompatActivity() {

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
            val imageUploaded = remember { mutableStateOf(false) }
            imageUploaded.value = intent.getBooleanExtra("imageUploaded", false)

            UI(
                context = this,
                authentication = authentication,
                imageUploaded = imageUploaded,
                name = intent.getStringExtra("Name") ?: "",
            )
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        this.startActivity(Intent(this, HomeScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}

@Composable
@Suppress("DEPRECATION")
fun UI(
    context: Context,
    name: String = "",
    authentication: FirebaseAuth,
    imageUploaded: MutableState<Boolean>
) {
    var userName by remember { mutableStateOf(name) }
    val connectivityChecker = ConnectivityChecker(context)
    val passWord by remember { mutableStateOf(" ") }
    var uploaded by remember { mutableStateOf(false) }
    var editable by remember { mutableStateOf(false) }
    var userNameSaved by remember { mutableStateOf(userName) }
    var passWordSaved by remember { mutableStateOf(" ") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val email by remember { mutableStateOf(authentication.currentUser!!.email ?: "") }
    var emailSaved by remember { mutableStateOf(authentication.currentUser!!.email ?: "") }
    val networkStatus by connectivityChecker.observeStatus()
        .collectAsState(initial = ConnectivityChecker.Status.Available)
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(),
        )
    }

    Column {
        if (networkStatus == ConnectivityChecker.Status.Available) {
            Column {
                Spacer(modifier = Modifier.height(40.dp))

                IconButton(modifier = Modifier.padding(8.dp), onClick = {
                    val intent = Intent(context, HomeScreen::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    intent.putExtra("imageUploaded", imageUploaded.value)
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back Arrow Button")
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Box(modifier = Modifier.height(40.dp)) {
                IconButton(modifier = Modifier
                    .padding(start = 30.dp, bottom = 0.dp, end = 20.dp)
                    .size(350.dp)
                    .scale(2.5f), onClick = {
                    launcher.launch("image/*")
                    uploaded = true
                }) {
                    imageUri?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap.value =
                                MediaStore.Images.Media.getBitmap(context.contentResolver, it)

                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            bitmap.value = ImageDecoder.decodeBitmap(source)
                        }

                    }
                    if (bitmap.value != null) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(100))
                                .background(Color.White), contentAlignment = Alignment.Center
                        ) {
                            Image(

                                painter = BitmapPainter(image = bitmap.value!!.asImageBitmap()),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Icon",
                            modifier = Modifier.size(256.dp)
                        )
                    }
                }

            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 0.dp, end = 16.dp, top = 30.dp)
                    .fillMaxWidth()
                    .height(356.dp)
                    .background(Color.White.copy(0.5f), MaterialTheme.shapes.medium)
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp)
                        .height(35.dp)
                        .width(80.dp),
                    onClick = {
                        editable = !editable
                        if (!editable) {
                            userNameSaved = userName
                            passWordSaved = passWord
                            emailSaved = email

                            updateNameInFireStore(authentication.currentUser!!, userName, context)
                        }
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = ButtonDefaults.buttonElevation(100.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = if (editable) "Save" else "Edit",
                        color = Color.Black,
                    )
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (editable) {
                        TextField(
                            value = userName,
                            onValueChange = { userName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            label = { Text("User Name") },
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .padding(start = 8.dp, bottom = 0.dp, end = 8.dp)
                        ) {
                            Text(
                                text = "Email",
                                fontSize = 15.sp,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = email,
                                fontSize = 20.sp,
                                maxLines = 1,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Divider(
                                color = Color.Black,
                                thickness = 0.5.dp,
                            )
                        }
                    } else {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                text = "Name",
                                fontSize = 15.sp,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = userName,
                                fontSize = 22.sp,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 6.dp)

                            )
                            Divider(
                                color = Color.Black,
                                thickness = 0.5.dp,
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .padding(start = 8.dp, bottom = 0.dp, end = 8.dp)
                        ) {
                            Text(
                                text = "Email",
                                fontSize = 15.sp,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = email,
                                fontSize = 20.sp,
                                maxLines = 1,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Divider(
                                color = Color.Black,
                                thickness = 0.5.dp,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp)
                                .padding(start = 8.dp, bottom = 0.dp, end = 8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    authentication.sendPasswordResetEmail(email)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context, "Mail have been send", Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    logOut(context)
                                }, modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "Change Password",
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End)
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .padding(15.dp)
                        .height(35.dp)
                        .width(120.dp),
                    onClick = {
                        logOut(context)
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = ButtonDefaults.buttonElevation(100.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = "Log Out",
                        color = Color.Black,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Version 1.0",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.baseline_wifi_off_24
                    ),
                    contentDescription = "No Internet Connection Icon",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Black
                )

                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        start = 16.dp, end = 0.dp, top = 0.dp, bottom = 0.dp
                    )
                )
            }
        }
    }
}

private fun logOut(context: Context) {
    FirebaseAuth.getInstance().signOut()

    context.startActivity(Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    })
}

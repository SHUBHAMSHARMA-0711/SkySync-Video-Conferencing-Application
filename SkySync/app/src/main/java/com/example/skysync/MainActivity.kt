package com.example.skysync

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.skysync.ui.theme.SkySyncTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

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
                    Main(authentication = authentication, activity = this)
                }
            }
        }
    }
}

@Composable
fun Main(
    authentication: FirebaseAuth = FirebaseAuth.getInstance(),
    activity: MainActivity = MainActivity()
) {
    AuthenticationScreen(authentication = authentication, context = activity)
}

@Composable
@Preview(showBackground = true)
fun MainPreview() {
    SkySyncTheme {
        Main()
    }
}

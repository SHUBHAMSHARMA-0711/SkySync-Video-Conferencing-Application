package com.example.skysync

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.zegocloud.uikit.components.audiovideo.ZegoAvatarViewProvider
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment
import java.util.Random

class ConferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            val userName = intent.getStringExtra("UserName")
            val meetingID = intent.getStringExtra("MeetingID")

            if (userName != null && meetingID != null) {
                addFragment(userName, meetingID, this)
            }
        }
    }

    private fun addFragment(username: String, meetingID: String, context: Context) {

        val appID: Long = 604276988
        val appSign = "1d91b4b772c66e6aeba250c5cf1fe4ec83de49327a4ee3faa13e24b4952a0ee2"
        val config = ZegoUIKitPrebuiltVideoConferenceConfig()

        config.avatarViewProvider = ZegoAvatarViewProvider { parent, _ ->
            val imageView = ImageView(parent.context)

            Glide.with(context).load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                .apply(RequestOptions().circleCrop()).into(imageView)

            imageView
        }

        config.turnOnCameraWhenJoining = false
        config.turnOnMicrophoneWhenJoining = false

        val fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(
            appID, appSign, generateUserID(), username, meetingID, config
        )

        supportFragmentManager.beginTransaction().replace(android.R.id.content, fragment)
            .commitNow()
    }

    private fun generateUserID(): String {

        val builder = StringBuilder()

        while (builder.length < 5) {
            val nextInt = Random().nextInt(10)

            if (builder.isEmpty() && nextInt == 0) continue

            builder.append(nextInt)
        }

        return builder.toString()
    }
}

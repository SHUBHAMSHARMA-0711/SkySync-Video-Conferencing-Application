package com.example.skysync

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

data class MeetingDetails(
    val title: String,
    val code: String,
    val time: String = "",
    val isScheduled: Boolean = false,
)

fun addSavedMeetings(
    user: FirebaseUser, meetingDetails: MeetingDetails, document: String = meetingDetails.title
) {
    deleteSavedMeetings(user, document)
    Firebase.firestore.collection(user.uid).document(meetingDetails.title)
        .set(meetingDetails, SetOptions.merge()).addOnSuccessListener {}.addOnFailureListener {}
}

fun updateNameInFireStore(user: FirebaseUser, newName: String, context: Context) {
    FirebaseFirestore.getInstance().collection("users").document(user.uid).update("name", newName)
        .addOnSuccessListener {
            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show()
        }
}

fun deleteSavedMeetings(
    user: FirebaseUser, title: String
) {
    Firebase.firestore.collection(user.uid).document(title).delete().addOnSuccessListener {}
        .addOnFailureListener {}
}

fun getSavedMeetings(
    i: MutableState<Int>,
    user: FirebaseUser,
    meetings: MutableState<List<String>>,
    meetingCodeList: MutableState<List<String>>,
    scheduledMeetings: MutableState<List<String>>,
    scheduledMeetingsDates: MutableState<List<String>>,
    scheduledMeetingsTimes: MutableState<List<String>>,
    scheduledMeetingCodeList: MutableState<List<String>>
) {
    FirebaseFirestore.getInstance().collection(user.uid).get().addOnSuccessListener { documents ->
        for (document in documents) {
            val title = document.getString("title")
            val meetingCode = document.getString("code")
            val time = document.getString("time")?.split(";")
            val isScheduled = document.getBoolean("scheduled")

            if (title != null && meetingCode != null && !meetings.value.contains(title)) {
                meetings.value += title
                meetingCodeList.value += meetingCode
            }

            if (isScheduled != null && time != null && isScheduled) {
                scheduledMeetings.value += title!!
                scheduledMeetingsDates.value += time[0]
                scheduledMeetingsTimes.value += time[1]
                scheduledMeetingCodeList.value += meetingCode!!
            }

            i.value += 1
        }
    }.addOnFailureListener {}
}

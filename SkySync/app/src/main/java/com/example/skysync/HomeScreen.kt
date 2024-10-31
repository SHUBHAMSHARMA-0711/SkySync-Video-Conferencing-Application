package com.example.skysync

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeScreen : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var authentication: FirebaseAuth = Firebase.auth
    private var sensorData: MutableState<Float> = mutableFloatStateOf(0f)

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

            val i = remember { mutableIntStateOf(0) }
            val meetings = remember { mutableStateOf(listOf<String>()) }
            val meetingCodeList = remember { mutableStateOf(listOf<String>()) }
            val scheduledMeetings = remember { mutableStateOf(listOf<String>()) }
            val scheduledMeetingsDates = remember { mutableStateOf(listOf<String>()) }
            val scheduledMeetingsTimes = remember { mutableStateOf(listOf<String>()) }
            val scheduledMeetingCodeList = remember { mutableStateOf(listOf<String>()) }

            getSavedMeetings(
                i,
                authentication.currentUser!!,
                meetings,
                meetingCodeList,
                scheduledMeetings,
                scheduledMeetingsDates,
                scheduledMeetingsTimes,
                scheduledMeetingCodeList
            )

            HomeScreen(
                context = LocalContext.current,
                authentication = authentication,
                meetings = meetings,
                meetingCodeList = meetingCodeList,
                scheduledMeetings = scheduledMeetings,
                scheduledMeetingsDates = scheduledMeetingsDates,
                scheduledMeetingsTimes = scheduledMeetingsTimes,
                scheduledMeetingCodeList = scheduledMeetingCodeList,
                sensorData = sensorData,
                user = authentication.currentUser!!,
                imageUploaded = intent.getBooleanExtra("imageUploaded", false)
            )
        }

        sensorManager = getSystemService(SensorManager::class.java)
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {

            sensorData.value = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    context: Context = LocalContext.current,
    authentication: FirebaseAuth,
    meetings: MutableState<List<String>>,
    meetingCodeList: MutableState<List<String>>,
    scheduledMeetings: MutableState<List<String>>,
    scheduledMeetingsDates: MutableState<List<String>>,
    scheduledMeetingsTimes: MutableState<List<String>>,
    scheduledMeetingCodeList: MutableState<List<String>>,
    sensorData: MutableState<Float>,
    user: FirebaseUser,
    imageUploaded: Boolean
) {
    val color = Color.Black
    var date = rememberDatePickerState()
    var time = rememberTimePickerState(is24Hour = true)
    val text = remember { mutableStateOf("") }
    val code = remember { mutableStateOf("") }
    val title = remember { mutableStateOf("") }
    val newCode = remember { mutableStateOf("") }
    val newTitle = remember { mutableStateOf("") }
    val userName = remember { mutableStateOf(" ") }
    val addCode = remember { mutableStateOf(false) }
    val addTitle = remember { mutableStateOf(false) }
    val meetingCode = remember { mutableStateOf("") }
    val newDialog = remember { mutableStateOf(false) }
    val joinDialog = remember { mutableStateOf(false) }
    val editDialog = remember { mutableStateOf(false) }
    val dateDialog = remember { mutableStateOf(false) }
    val timeDialog = remember { mutableStateOf(false) }
    val directJoin = remember { mutableStateOf(false) }
    val currentIndex = remember { mutableIntStateOf(0) }
    val codeChanged = remember { mutableStateOf(false) }
    val addNewDialog = remember { mutableStateOf(false) }
    val titleChanged = remember { mutableStateOf(false) }
    val deleteDialog = remember { mutableStateOf(false) }
    val deleteMeeting = remember { mutableStateOf(false) }
    val formattedDate = remember { mutableStateOf("") }
    val formattedTime = remember { mutableStateOf("") }
    val dateTimeDialog = remember { mutableStateOf(false) }
    val scheduleMeeting = remember { mutableStateOf(false) }
    val deleteScheduledDialog = remember { mutableStateOf(false) }
    val deleteScheduledMeeting = remember { mutableStateOf(false) }
    val connectivityChecker = ConnectivityChecker(context)
    val networkStatus by connectivityChecker.observeStatus()
        .collectAsState(initial = ConnectivityChecker.Status.Available)
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    FirebaseFirestore.getInstance().collection("users").document(user.uid).get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                if (document.exists()) {
                    userName.value = document.getString("name").toString()
                }
            }
        }.addOnFailureListener {}

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        deleteExpiredMeetings(
            scheduledMeetingsDates,
            scheduledMeetingsTimes,
            scheduledMeetings,
            scheduledMeetingCodeList,
            user
        )

        if (networkStatus == ConnectivityChecker.Status.Available) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(50.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(
                                context, ProfileScreen::class.java
                            ).putExtra("Name", userName.value)
                                .putExtra("imageUploaded", imageUploaded).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                        }, modifier = Modifier
                            .padding(
                                start = 16.dp, end = 30.dp, top = 0.dp, bottom = 0.dp
                            )
                            .size(70.dp)
                    ) {
                        if (bitmap.value != null) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(100))
                                    .background(Color.White), contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = bitmap.value!!.asImageBitmap(),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Profile Icon",
                                modifier = Modifier.size(70.dp),
                                tint = color
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Welcome, ${userName.value}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = color,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 16.dp, end = 0.dp, top = 0.dp, bottom = 0.dp
                        )
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ElevatedButton(
                            onClick = { newDialog.value = !newDialog.value },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = color
                            ),
                            border = BorderStroke(1.dp, color),
                            elevation = ButtonDefaults.buttonElevation(100.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.outline_videocam_24),
                                contentDescription = "Video Call Icon",
                                modifier = Modifier.size(30.dp),
                                tint = color
                            )

                            Text(
                                text = "New",
                                color = color,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        ElevatedButton(
                            onClick = { joinDialog.value = !joinDialog.value },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = color
                            ),
                            border = BorderStroke(1.dp, color),
                            elevation = ButtonDefaults.buttonElevation(100.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_meeting_room_24),
                                contentDescription = "Join Call Icon",
                                modifier = Modifier.size(30.dp),
                                tint = color
                            )

                            Text(
                                text = "Join",
                                color = color,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(10.dp)
                            )

                        }
                    }
                }

                if (newDialog.value) {
                    meetingCode.value = (100000..999999).random().toString()
                    NewDialog(
                        newDialog = newDialog,
                        userName = userName,
                        meetingCode = meetingCode,
                        context = context,
                        sensorData = sensorData
                    )
                }

                if (joinDialog.value) {
                    text.value = ""
                    JoinDialog(
                        joinDialog = joinDialog,
                        text = text,
                        userName = userName,
                        context = context,
                        sensorData = sensorData
                    )
                }

                Text(
                    text = "Scheduled",
                    style = MaterialTheme.typography.bodyLarge,
                    color = color,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        start = 16.dp, end = 0.dp, top = 20.dp, bottom = 0.dp
                    )
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFE2EBEF)
                        ),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(80.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 16.dp
                        ),
                    ) {
                        if (scheduledMeetings.value.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.padding(7.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(scheduledMeetings.value.size) { index ->
                                    ElevatedCard(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 0.dp
                                        ),
                                        modifier = Modifier
                                            .size(width = 400.dp, height = 55.dp)
                                            .padding(5.dp),
                                        colors = CardDefaults.elevatedCardColors(
                                            containerColor = Color(0xFFC4E8FF),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .width(250.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = scheduledMeetings.value[index],
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontSize = 16.sp,
                                                        modifier = Modifier.padding(
                                                            start = 16.dp,
                                                            end = 0.dp,
                                                            top = 4.dp,
                                                            bottom = 0.dp
                                                        )
                                                    )

                                                    Column(
                                                        horizontalAlignment = Alignment.Start,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = scheduledMeetingsTimes.value[index],
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontSize = 14.sp,
                                                            modifier = Modifier.padding(
                                                                start = 16.dp,
                                                                end = 0.dp,
                                                                top = 3.dp,
                                                                bottom = 0.dp
                                                            )
                                                        )
                                                        Text(
                                                            text = scheduledMeetingsDates.value[index],
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontSize = 14.sp,
                                                            modifier = Modifier.padding(
                                                                start = 16.dp,
                                                                end = 0.dp,
                                                                top = 2.dp,
                                                                bottom = 3.dp
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                            Column(
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .width(50.dp),
                                                    horizontalArrangement = Arrangement.End,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            currentIndex.intValue = index
                                                            deleteScheduledDialog.value =
                                                                !deleteScheduledDialog.value
                                                        }, modifier = Modifier.size(25.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(
                                                                id = R.drawable.baseline_delete_outline_24
                                                            ),
                                                            contentDescription = "Delete Button",
                                                            modifier = Modifier.size(24.dp),
                                                            tint = Color.Black
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "No Meetings Scheduled",
                                    color = color,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp),
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Saved",
                    style = MaterialTheme.typography.bodyLarge,
                    color = color,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        start = 16.dp, end = 0.dp, top = 10.dp, bottom = 0.dp
                    )
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xffe2ebef)
                        ),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(190.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 16.dp
                        ),
                    ) {
                        if (meetings.value.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.padding(7.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(meetings.value.size) { index ->
                                    ElevatedCard(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 0.dp
                                        ),
                                        modifier = Modifier
                                            .size(width = 400.dp, height = 55.dp)
                                            .padding(5.dp),
                                        colors = CardDefaults.elevatedCardColors(
                                            containerColor = Color(0xFFC4E8FF),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .width(200.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(
                                                    text = meetings.value[index],
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier.padding(
                                                        start = 16.dp,
                                                        end = 0.dp,
                                                        top = 4.dp,
                                                        bottom = 0.dp
                                                    )
                                                )
                                            }

                                            Column(
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .width(125.dp),
                                                    horizontalArrangement = Arrangement.SpaceAround,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            code.value =
                                                                meetingCodeList.value[index]
                                                            currentIndex.intValue = index
                                                            directJoin.value = !directJoin.value
                                                        }, modifier = Modifier.size(25.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(
                                                                id = R.drawable.baseline_meeting_room_24
                                                            ),
                                                            contentDescription = "Join Button",
                                                            modifier = Modifier.size(24.dp),
                                                            tint = Color.Black
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            currentIndex.intValue = index
                                                            dateTimeDialog.value =
                                                                !dateTimeDialog.value
                                                        }, modifier = Modifier.size(25.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(
                                                                id = R.drawable.baseline_schedule_24
                                                            ),
                                                            contentDescription = "Schedule Button",
                                                            modifier = Modifier.size(24.dp),
                                                            tint = Color.Black
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            share(
                                                                meetingTitle = meetings.value[index],
                                                                meetingCode = meetingCodeList.value[index],
                                                                context = context
                                                            )
                                                        }, modifier = Modifier.size(25.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Share,
                                                            contentDescription = "Share Button",
                                                            modifier = Modifier.size(24.dp),
                                                            tint = Color.Black
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            title.value = meetings.value[index]
                                                            code.value =
                                                                meetingCodeList.value[index]
                                                            editDialog.value = !editDialog.value
                                                            currentIndex.intValue = index
                                                        }, modifier = Modifier.size(25.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(
                                                                id = R.drawable.outline_edit_24
                                                            ),
                                                            contentDescription = "Edit Button",
                                                            modifier = Modifier.size(24.dp),
                                                            tint = Color.Black
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "No Meetings Saved",
                                    color = color,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp),
                                )
                            }
                        }
                    }

                    if (dateTimeDialog.value) {
                        DateTimeDialog(
                            dateTimeDialog = dateTimeDialog,
                            dateDialog = dateDialog,
                            timeDialog = timeDialog,
                            scheduleMeeting = scheduleMeeting
                        )
                    }

                    if (dateDialog.value) {
                        DateDialog(
                            dateDialog = dateDialog,
                            date = date,
                            formattedDate = formattedDate,
                            scheduleMeeting = scheduleMeeting,
                        )
                    }

                    if (timeDialog.value) {
                        TimeDialog(
                            timeDialog = timeDialog,
                            time = time,
                            formattedTime = formattedTime,
                            scheduleMeeting = scheduleMeeting,
                        )
                    }

                    if (scheduleMeeting.value && !scheduledMeetings.value.contains(meetings.value[currentIndex.intValue])) {

                        val currentDate =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val currentTime =
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                        if (formattedDate.value < currentDate || (formattedDate.value == currentDate && formattedTime.value <= currentTime)) {
                            Toast.makeText(
                                context, "Please Select a Future Date and Time", Toast.LENGTH_SHORT
                            ).show()
                            scheduleMeeting.value = false
                        } else {
                            val intent = Intent(context, NotificationActivity::class.java).apply {}
                            intent.putExtra("Date", formattedDate.value + ";" + formattedTime.value)
                            intent.putExtra("toRemove", false)
                            intent.putExtra("Title", "Reminder, Upcoming Meeting")
                            intent.putExtra(
                                "Message",
                                "Join the Meeting : ${meetings.value[currentIndex.intValue]}"
                            )
                            context.startActivity(intent)

                            addMeeting(scheduledMeetings, meetings.value[currentIndex.intValue])
                            addMeeting(scheduledMeetingsDates, formattedDate.value)
                            addMeeting(scheduledMeetingsTimes, formattedTime.value)
                            addMeeting(
                                scheduledMeetingCodeList,
                                meetingCodeList.value[currentIndex.intValue]
                            )

                            val meetingIndex =
                                meetings.value.indexOf(meetings.value[currentIndex.intValue])

                            addSavedMeetings(
                                user = authentication.currentUser!!,
                                meetingDetails = MeetingDetails(
                                    title = meetings.value[meetingIndex],
                                    code = meetingCodeList.value[meetingIndex],
                                    time = formattedDate.value + ";" + formattedTime.value,
                                    isScheduled = true
                                )
                            )

                            date = rememberDatePickerState()
                            time = rememberTimePickerState(is24Hour = true)
                            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()
                        }

                        scheduleMeeting.value = false

                    } else if (scheduleMeeting.value) {
                        Toast.makeText(
                            context, "Meeting Already Scheduled", Toast.LENGTH_SHORT
                        ).show()
                        scheduleMeeting.value = false
                    }

                    if (directJoin.value) {
                        directJoin(
                            directJoin = directJoin,
                            context = context,
                            userName = userName.value,
                            code = code.value
                        )
                    }

                    if (editDialog.value) {
                        EditDialog(
                            editDialog = editDialog,
                            title = title,
                            code = code,
                            titleChanged = titleChanged,
                            codeChanged = codeChanged,
                            deleteDialog = deleteDialog
                        )
                    }

                    if (titleChanged.value && codeChanged.value) {

                        val document = meetings.value[currentIndex.intValue]
                        val exists = scheduledMeetings.value.contains(document)
                        val meetingIndex = scheduledMeetings.value.indexOf(document)

                        meetings.value = meetings.value.toMutableList().apply {
                            set(currentIndex.intValue, title.value)
                        }

                        meetingCodeList.value = meetingCodeList.value.toMutableList().apply {
                            set(currentIndex.intValue, code.value)
                        }

                        if (exists) {
                            scheduledMeetings.value =
                                scheduledMeetings.value.toMutableList().apply {
                                    set(meetingIndex, title.value)
                                }

                            scheduledMeetingCodeList.value =
                                scheduledMeetingCodeList.value.toMutableList().apply {
                                    set(meetingIndex, code.value)
                                }
                        }

                        addSavedMeetings(
                            user = authentication.currentUser!!, meetingDetails = MeetingDetails(
                                title = title.value,
                                code = code.value,
                                time = if (exists) scheduledMeetingsDates.value[meetingIndex] + ";" + scheduledMeetingsTimes.value[meetingIndex] else "",
                                isScheduled = exists
                            ), document = document
                        )

                        Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()

                        titleChanged.value = false
                        codeChanged.value = false
                    }

                    if (titleChanged.value && !meetings.value.contains(title.value) && !codeChanged.value) {

                        val document = meetings.value[currentIndex.intValue]

                        meetings.value = meetings.value.toMutableList().apply {
                            set(currentIndex.intValue, title.value)
                        }

                        addSavedMeetings(
                            user = authentication.currentUser!!, meetingDetails = MeetingDetails(
                                title = title.value,
                                code = meetingCodeList.value[currentIndex.intValue],
                            ), document = document
                        )

                        titleChanged.value = false
                    } else if (titleChanged.value && !codeChanged.value) {
                        Toast.makeText(
                            context, "Meeting Already Exists with same Details", Toast.LENGTH_SHORT
                        ).show()
                        titleChanged.value = false
                    }

                    if (codeChanged.value && !meetingCodeList.value.contains(code.value) && !titleChanged.value) {
                        meetingCodeList.value = meetingCodeList.value.toMutableList().apply {
                            set(currentIndex.intValue, code.value)
                        }

                        addSavedMeetings(
                            user = authentication.currentUser!!, meetingDetails = MeetingDetails(
                                title = meetings.value[currentIndex.intValue],
                                code = code.value,
                            )
                        )

                        codeChanged.value = false
                    } else if (codeChanged.value && !titleChanged.value) {
                        Toast.makeText(
                            context, "Meeting Already Exists with same Details", Toast.LENGTH_SHORT
                        ).show()
                        codeChanged.value = false
                    }

                    if (deleteDialog.value) {
                        DeleteDialog(deleteMeeting = deleteMeeting, deleteDialog = deleteDialog)
                    }

                    if (deleteMeeting.value) {

                        deleteSavedMeetings(
                            user = authentication.currentUser!!,
                            title = meetings.value[currentIndex.intValue]
                        )

                        deleteMeeting(meetings, currentIndex.intValue)
                        deleteMeeting(meetingCodeList, currentIndex.intValue)

                        if (scheduledMeetings.value.isNotEmpty()) {
                            deleteMeeting(scheduledMeetings, currentIndex.intValue)
                            deleteMeeting(scheduledMeetingsTimes, currentIndex.intValue)
                            deleteMeeting(scheduledMeetingsDates, currentIndex.intValue)
                            deleteMeeting(scheduledMeetingCodeList, currentIndex.intValue)
                        }

                        Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()

                        deleteMeeting.value = false
                    }

                    if (deleteScheduledDialog.value) {
                        DeleteDialog(
                            deleteMeeting = deleteScheduledMeeting,
                            deleteDialog = deleteScheduledDialog
                        )
                    }

                    if (deleteScheduledMeeting.value) {

                        val intent = Intent(context, NotificationActivity::class.java).apply {}
                        intent.putExtra("Date", formattedDate.value + ";" + formattedTime.value)
                        intent.putExtra("toRemove", true)
                        intent.putExtra("Title", "Reminder, Upcoming Meeting")
                        intent.putExtra("Message", "Join the Meeting :")
                        context.startActivity(intent)

                        val meetingIndex =
                            meetings.value.indexOf(scheduledMeetings.value[currentIndex.intValue])

                        deleteMeeting(scheduledMeetings, currentIndex.intValue)
                        deleteMeeting(scheduledMeetingsTimes, currentIndex.intValue)
                        deleteMeeting(scheduledMeetingsDates, currentIndex.intValue)
                        deleteMeeting(scheduledMeetingCodeList, currentIndex.intValue)

                        addSavedMeetings(
                            user = authentication.currentUser!!, meetingDetails = MeetingDetails(
                                title = meetings.value[meetingIndex],
                                code = meetingCodeList.value[meetingIndex],
                                time = "",
                                isScheduled = false
                            )
                        )

                        Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()

                        deleteScheduledMeeting.value = false
                    }

                    ElevatedButton(
                        onClick = { addNewDialog.value = !addNewDialog.value },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                            .width(190.dp)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, contentColor = color
                        ),
                        border = BorderStroke(1.dp, color),
                        elevation = ButtonDefaults.buttonElevation(100.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.outline_playlist_add_24),
                            contentDescription = "Save New Meeting Button",
                            modifier = Modifier.size(30.dp),
                            tint = color
                        )

                        Text(
                            text = "Add New",
                            color = color,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }

                    if (addNewDialog.value) {
                        AddNewDialog(
                            addNewDialog = addNewDialog,
                            title = newTitle,
                            code = newCode,
                            addTitle = addTitle,
                            addCode = addCode
                        )
                    }

                    if (addTitle.value && addCode.value && !meetings.value.contains(newTitle.value)) {

                        addMeeting(meetings, newTitle.value)
                        addMeeting(meetingCodeList, newCode.value)

                        addSavedMeetings(
                            user = authentication.currentUser!!, meetingDetails = MeetingDetails(
                                title = newTitle.value, code = newCode.value
                            )
                        )

                        Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()

                        addCode.value = false
                        addTitle.value = false
                    } else if (addTitle.value && addCode.value) {
                        Toast.makeText(
                            context, "Meeting Already Saved", Toast.LENGTH_SHORT
                        ).show()
                        addCode.value = false
                        addTitle.value = false
                    }
                }
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
                    color = color,
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

private fun directJoin(
    directJoin: MutableState<Boolean>, userName: String, code: String, context: Context
) {
    directJoin.value = !directJoin.value

    val intent = Intent(context, ConferenceActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    intent.putExtra("UserName", userName)
    intent.putExtra("MeetingID", code)
    context.startActivity(intent)
}

fun share(meetingTitle: String = "", meetingCode: String, context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND

        if (meetingTitle.isNotEmpty()) {
            putExtra(
                Intent.EXTRA_TEXT, "Join the Meeting : $meetingTitle using this Code : $meetingCode"
            )
        } else {
            putExtra(Intent.EXTRA_TEXT, "Join the Meeting using this Code : $meetingCode")
        }
        type = "text/plain"
    }

    context.startActivity(Intent.createChooser(sendIntent, null))
}

private fun addMeeting(meeting: MutableState<List<String>>, value: String) {
    meeting.value = meeting.value.toMutableList().apply {
        add(value)
    }
}

private fun deleteMeeting(meetings: MutableState<List<String>>, index: Int) {
    meetings.value = meetings.value.toMutableList().apply {
        removeAt(index)
    }
}

private fun deleteExpiredMeetings(
    scheduledMeetingsDates: MutableState<List<String>>,
    scheduledMeetingsTimes: MutableState<List<String>>,
    scheduledMeetings: MutableState<List<String>>,
    scheduledMeetingCodeList: MutableState<List<String>>,
    user: FirebaseUser
) {
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    for (i in scheduledMeetingsDates.value.indices) {
        if (scheduledMeetingsDates.value[i] < currentDate || (scheduledMeetingsDates.value[i] == currentDate && scheduledMeetingsTimes.value[i] <= currentTime)) {

            addSavedMeetings(
                user = user, meetingDetails = MeetingDetails(
                    title = scheduledMeetings.value[i],
                    code = scheduledMeetingCodeList.value[i],
                    time = "",
                    isScheduled = false
                )
            )

            deleteMeeting(scheduledMeetings, i)
            deleteMeeting(scheduledMeetingsTimes, i)
            deleteMeeting(scheduledMeetingsDates, i)
            deleteMeeting(scheduledMeetingCodeList, i)
        }
    }
}

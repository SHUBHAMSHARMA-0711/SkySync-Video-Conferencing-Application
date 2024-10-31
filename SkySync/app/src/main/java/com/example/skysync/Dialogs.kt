package com.example.skysync

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun NewDialog(
    newDialog: MutableState<Boolean>,
    userName: MutableState<String>,
    meetingCode: MutableState<String>,
    sensorData: MutableState<Float>,
    context: Context
) {
    AlertDialog(onDismissRequest = { newDialog.value = !newDialog.value },
        icon = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_videocam_24),
                    contentDescription = "Video Call Icon",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )

                if (sensorData.value <= 20.0f) {
                    Text(
                        text = "Poor Lighting Conditions",
                        textAlign = TextAlign.Center,
                        color = Color.Red
                    )
                }
            }
        },

        title = {
            Text(
                text = "Meeting Code : ${meetingCode.value}", textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(text = "Share this Code with Other's", textAlign = TextAlign.Center)
            }
        },
        containerColor = Color.Black,
        titleContentColor = Color.White,
        textContentColor = Color.White,

        confirmButton = {
            TextButton(onClick = {
                newDialog.value = !newDialog.value
                val intent = Intent(context, ConferenceActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                intent.putExtra("UserName", userName.value)
                intent.putExtra("MeetingID", meetingCode.value)
                context.startActivity(intent)
            }) {
                Text(text = "START", color = Color.White)
            }
        },

        dismissButton = {
            TextButton(onClick = {
                newDialog.value = newDialog.value
                share(meetingCode = meetingCode.value, context = context)
            }) {
                Text(text = "SHARE", color = Color.White)
            }
        })
}

@Composable
fun JoinDialog(
    joinDialog: MutableState<Boolean>,
    text: MutableState<String>,
    userName: MutableState<String>,
    sensorData: MutableState<Float>,
    context: Context
) {
    AlertDialog(onDismissRequest = { joinDialog.value = !joinDialog.value }, icon = {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_meeting_room_24),
                contentDescription = "Video Call Icon",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )

            if (sensorData.value <= 20.0f) {
                Text(
                    text = "Poor Lighting Conditions",
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
            }
        }
    }, title = {
        Text(text = "Join Meeting", textAlign = TextAlign.Center)
    },

        text = {
            OutlinedTextField(value = text.value,
                onValueChange = { text.value = it },
                label = { Text("Enter Meeting Code") },
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                placeholder = { Text("Enter Meeting Code") })
        }, containerColor = Color.Black, titleContentColor = Color.White,

        confirmButton = {
            TextButton(onClick = {
                val intent = Intent(context, ConferenceActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                intent.putExtra("UserName", userName.value)
                intent.putExtra("MeetingID", text.value)
                context.startActivity(intent)
                joinDialog.value = !joinDialog.value
                text.value = ""
            }) {
                Text(text = "JOIN", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },

        dismissButton = {
            TextButton(onClick = {
                joinDialog.value = !joinDialog.value
                text.value = ""
            }) {
                Text(text = "CANCEL", color = Color.White, fontWeight = FontWeight.Bold)
            }
        })
}

@Composable
fun EditDialog(
    editDialog: MutableState<Boolean>,
    title: MutableState<String>,
    code: MutableState<String>,
    titleChanged: MutableState<Boolean>,
    codeChanged: MutableState<Boolean>,
    deleteDialog: MutableState<Boolean>
) {
    AlertDialog(onDismissRequest = { editDialog.value = !editDialog.value },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.outline_edit_24),
                contentDescription = "Edit Icon",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        },
        title = {
            Text(text = "Edit Details", textAlign = TextAlign.Center)
        },
        text = {
            Column {
                OutlinedTextField(value = title.value, onValueChange = {
                    title.value = it
                }, label = { Text("Title") }, colors = TextFieldDefaults.colors(
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ), placeholder = { Text("Enter New Title") })

                OutlinedTextField(value = code.value, onValueChange = {
                    code.value = it
                }, label = { Text("Code") }, colors = TextFieldDefaults.colors(
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ), placeholder = { Text("Enter New Code") })
            }
        },

        containerColor = Color.Black,
        titleContentColor = Color.White,
        textContentColor = Color.White,

        confirmButton = {
            TextButton(onClick = {
                deleteDialog.value = true
                editDialog.value = !editDialog.value
            }) {
                Text(text = "DELETE", color = Color.White)
            }

            TextButton(onClick = {
                if (title.value.isNotEmpty()) {
                    titleChanged.value = !titleChanged.value
                }
                if (code.value.isNotEmpty()) {
                    codeChanged.value = !codeChanged.value
                }
                editDialog.value = !editDialog.value
            }) {
                Text(text = "SAVE", color = Color.White)
            }
        },

        dismissButton = {
            TextButton(onClick = {
                editDialog.value = !editDialog.value
            }) {
                Text(text = "CANCEL", color = Color.White)
            }
        })
}

@Composable
fun DeleteDialog(
    deleteMeeting: MutableState<Boolean>, deleteDialog: MutableState<Boolean>
) {
    AlertDialog(onDismissRequest = { deleteDialog.value = !deleteDialog.value },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_outline_24),
                contentDescription = "Delete Icon",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        },
        title = {
            Text(text = "Delete Meeting", textAlign = TextAlign.Center)
        },
        text = {
            Text(
                text = "Are you sure you want to delete this meeting?", textAlign = TextAlign.Center
            )
        },

        containerColor = Color.Black,
        titleContentColor = Color.White,
        textContentColor = Color.White,

        confirmButton = {
            TextButton(onClick = {
                deleteMeeting.value = true
                deleteDialog.value = !deleteDialog.value
            }) {
                Text(text = "DELETE", color = Color.White)
            }
        },

        dismissButton = {
            TextButton(onClick = {
                deleteMeeting.value = false
                deleteDialog.value = !deleteDialog.value
            }) {
                Text(text = "CANCEL", color = Color.White)
            }
        })
}

@Composable
fun AddNewDialog(
    addNewDialog: MutableState<Boolean>,
    title: MutableState<String>,
    code: MutableState<String>,
    addTitle: MutableState<Boolean>,
    addCode: MutableState<Boolean>
) {
    code.value = ""
    title.value = ""
    AlertDialog(onDismissRequest = { addNewDialog.value = !addNewDialog.value },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.outline_playlist_add_24),
                contentDescription = "Add New Icon",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        },
        title = {
            Text(text = "Save Meeting", textAlign = TextAlign.Center)
        },
        text = {
            Column {
                OutlinedTextField(value = title.value, onValueChange = {
                    title.value = it
                }, label = { Text("Title") }, colors = TextFieldDefaults.colors(
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ), placeholder = { Text("Enter Title") })

                OutlinedTextField(value = code.value, onValueChange = {
                    code.value = it
                }, label = { Text("Code") }, colors = TextFieldDefaults.colors(
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ), placeholder = { Text("Enter Code") })
            }
        },

        containerColor = Color.Black,
        titleContentColor = Color.White,
        textContentColor = Color.White,

        confirmButton = {
            TextButton(onClick = {
                if (title.value.isNotEmpty() && code.value.isNotEmpty()) {
                    addTitle.value = true
                    addCode.value = true
                }
                addNewDialog.value = !addNewDialog.value
            }) {
                Text(text = "SAVE", color = Color.White)
            }
        },

        dismissButton = {
            TextButton(onClick = {
                addNewDialog.value = !addNewDialog.value
            }) {
                Text(text = "CANCEL", color = Color.White)
            }
        })
}

@Composable
fun DateTimeDialog(
    dateTimeDialog: MutableState<Boolean>,
    dateDialog: MutableState<Boolean>,
    timeDialog: MutableState<Boolean>,
    scheduleMeeting: MutableState<Boolean>
) {
    AlertDialog(onDismissRequest = { dateTimeDialog.value = !dateTimeDialog.value },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Select Date & Time", textAlign = TextAlign.Center)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = {
                    dateDialog.value = !dateDialog.value
                }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_date_range_24),
                            contentDescription = "Date Icon",
                            modifier = Modifier.size(30.dp),
                            tint = Color.White
                        )

                        Text(
                            text = "Select Date",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }

                TextButton(onClick = {
                    timeDialog.value = !timeDialog.value
                }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
                            contentDescription = "Time Icon",
                            modifier = Modifier.size(30.dp),
                            tint = Color.White
                        )

                        Text(
                            text = "Select Time",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        },

        containerColor = Color.Black,
        titleContentColor = Color.White,
        textContentColor = Color.White,

        confirmButton = {
            TextButton(onClick = {
                dateTimeDialog.value = !dateTimeDialog.value
                scheduleMeeting.value = true
            }) {
                Text(text = "SAVE", color = Color.White)
            }
        },

        dismissButton = {
            TextButton(onClick = {
                dateTimeDialog.value = !dateTimeDialog.value
                scheduleMeeting.value = false
            }) {
                Text(text = "CANCEL", color = Color.White)
            }
        })
}

@Composable
@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
fun DateDialog(
    dateDialog: MutableState<Boolean>,
    date: DatePickerState,
    formattedDate: MutableState<String>,
    scheduleMeeting: MutableState<Boolean>
) {
    DatePickerDialog(onDismissRequest = { dateDialog.value = !dateDialog.value }, confirmButton = {
        TextButton(onClick = {
            dateDialog.value = !dateDialog.value

            if (date.selectedDateMillis != null) {
                val selectedDate =
                    Calendar.getInstance().apply { timeInMillis = date.selectedDateMillis!! }

                formattedDate.value = SimpleDateFormat("yyyy-MM-dd").format(selectedDate.time)

            } else {
                formattedDate.value = ""
            }

        }) {
            Text("SELECT", color = Color.White)
        }
    }, dismissButton = {
        TextButton(onClick = {
            dateDialog.value = !dateDialog.value
            scheduleMeeting.value = false
        }) {
            Text("CANCEL", color = Color.White)
        }
    }, colors = DatePickerDefaults.colors(containerColor = Color.Black)
    ) {
        DatePicker(
            state = date, colors = DatePickerDefaults.colors(
                containerColor = Color.Black,
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.White,
                subheadContentColor = Color.White,
                yearContentColor = Color.White,
                currentYearContentColor = Color.White,
                selectedYearContainerColor = Color.White,
                selectedYearContentColor = Color.Black,
                dayContentColor = Color.White,
                disabledDayContentColor = Color.White,
                selectedDayContentColor = Color.Black,
                disabledSelectedDayContentColor = Color.White,
                selectedDayContainerColor = Color.White,
                disabledSelectedDayContainerColor = Color.White,
                todayContentColor = Color.White,
                todayDateBorderColor = Color.White,
                dayInSelectionRangeContainerColor = Color.White,
                dayInSelectionRangeContentColor = Color.White,
            )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimeDialog(
    timeDialog: MutableState<Boolean>,
    time: TimePickerState,
    formattedTime: MutableState<String>,
    scheduleMeeting: MutableState<Boolean>
) {
    DatePickerDialog(onDismissRequest = { timeDialog.value = !timeDialog.value },
        confirmButton = {
            TextButton(onClick = {
                timeDialog.value = !timeDialog.value
                formattedTime.value =
                    time.hour.toString().padStart(2, '0') + ":" + time.minute.toString()
                        .padStart(2, '0')

            }) {
                Text("SELECT", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                timeDialog.value = !timeDialog.value
                scheduleMeeting.value = false
            }) {
                Text("CANCEL", color = Color.White)
            }
        },
        colors = DatePickerDefaults.colors(containerColor = Color.Black),
        modifier = Modifier
            .width(300.dp)
            .height(500.dp),
        shape = RoundedCornerShape(20.dp)
    ) {

        TimePicker(
            state = time,
            colors = TimePickerDefaults.colors(
                containerColor = Color.Black,
                selectorColor = Color.White,
                clockDialColor = Color.LightGray,
                clockDialSelectedContentColor = Color.Black,
                clockDialUnselectedContentColor = Color.Black,
                timeSelectorSelectedContentColor = Color.White,
                timeSelectorUnselectedContentColor = Color.White,
                timeSelectorSelectedContainerColor = Color.Black,
                timeSelectorUnselectedContainerColor = Color.Black,
                periodSelectorBorderColor = Color.White,
                periodSelectorSelectedContentColor = Color.Black,
                periodSelectorUnselectedContentColor = Color.White,
                periodSelectorSelectedContainerColor = Color.White,
                periodSelectorUnselectedContainerColor = Color.Black,

                ),
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(160.dp))
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
        )
    }
}

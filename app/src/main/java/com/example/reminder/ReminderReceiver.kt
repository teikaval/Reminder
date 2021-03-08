package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

//from exercises handler for the notifications
class ReminderReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context!= null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofencingTransition = geofencingEvent.geofenceTransition

            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                if (intent != null) {
                    // Retrieve data from intent
                    val uuid = intent.getIntExtra("uid", 0)
                    //val message: String = intent.getStringExtra("message").toString()
                    val reminderCalender = intent.getLongExtra("reminderDate", 0)
                    val heading = intent.getStringExtra("heading")
                    val details = intent.getStringExtra("details")
                    val date = intent.getStringExtra("date")
                    val message = "Remember reminder with heading $heading and message $details is due $date"
                    SecondScreen.setReminderWithWorkManager(
                        context,
                        uuid,
                        reminderCalender,
                        message
                    )
                }
            }
        }
    }
}
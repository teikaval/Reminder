package com.example.reminder

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.reminder.db.AppDatabase
import com.example.reminder.db.ReminderInfo
//this will workout the Editing of existing reminders
class EditReminderView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_reminder_view)
        //this is the id for the editable reminder taken from SecondScreen
        val reminderID = intent.getIntExtra("reminderID", 0)
        //if user cancels lets not do anything and return to SecondScreen
        findViewById<Button>(R.id.cancelReminderEditBtn).setOnClickListener {
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
        //if user presses the save button lets make changes to the database
        findViewById<Button>(R.id.editSaveChangesBtn).setOnClickListener {
            val reminderInfo = ReminderInfo(reminderID,
                    heading = findViewById<TextView>(R.id.editReminderHeading).text.toString(),
                    message = findViewById<TextView>(R.id.editReminderText).text.toString(),
                    reminder_time = findViewById<TextView>(R.id.editReminderDate).text.toString(),
                    creation_time = "testi",
                    creator_id = "testi",
                    reminder_seen = 0,
                    location_x = 0.0,
                    location_y = 0.0)

            AsyncTask.execute {
                val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, getString(R.string.dbFileName)).build()
                val uuid = db.reminderDao().updateReminder(reminderInfo)
                db.close()
            }
            //return back to the SecondScreen
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
    }
}
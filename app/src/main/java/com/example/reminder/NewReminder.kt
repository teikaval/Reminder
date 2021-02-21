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
//This activity will handle creation of new reminders
class NewReminder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_reminder)

        //if user cancels the creation of new reminder lets return back to the SecondsScreen
        findViewById<Button>(R.id.cancelReminderBtn).setOnClickListener {
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
        //if user presses the add button lets import new reminder to database
        findViewById<Button>(R.id.addReminderBtn).setOnClickListener{
            //here needs to be steps to save data to the room database
            val reminderInfo = ReminderInfo(null,
            heading = findViewById<TextView>(R.id.reminderHeadingtext).text.toString(),
            message = findViewById<TextView>(R.id.reminderTexttext).text.toString(),
            reminder_time = findViewById<TextView>(R.id.reminderDate).text.toString(),
            creation_time = "testi",
            creator_id = "testi",
            reminder_seen = 0,
            location_x = 0.0,
            location_y = 0.0,
            )

            AsyncTask.execute{
                val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, getString(R.string.dbFileName)).build()
                val uuid = db.reminderDao().insert(reminderInfo).toInt()
                db.close()
            }
            //lets return back to the SecondScreen after creation has completed
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
    }
}
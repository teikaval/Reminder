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

class NewReminder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_reminder)
        findViewById<Button>(R.id.cancelReminderBtn).setOnClickListener {
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }

        findViewById<Button>(R.id.addReminderBtn).setOnClickListener{
            //here needs to be steps to save data to the room database
            val reminderInfo = ReminderInfo(null,
            heading = findViewById<TextView>(R.id.reminderHeadingtext).text.toString(),
            text = findViewById<TextView>(R.id.reminderTexttext).text.toString(),
            date = "Testi",
            amount = "Testi")
            println(reminderInfo.heading)
            println("Testi")
            AsyncTask.execute{
                val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, getString(R.string.dbFileName)).build()
                val uuid = db.reminderDao().insert(reminderInfo).toInt()
                db.close()
            }
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
    }
}
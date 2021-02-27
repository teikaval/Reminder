package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.reminder.db.AppDatabase
import com.example.reminder.db.ReminderInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
            val reminderCalender = GregorianCalendar.getInstance()
            val dateFormat = "dd.MM.yyyy" // change this format to dd.MM.yyyy if you have not time in your date.
            // a better way of handling dates but requires API version 26 (Build.VERSION_CODES.O)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern(dateFormat)
                val date = LocalDate.parse(findViewById<TextView>(R.id.editReminderDate).text.toString(), formatter)

                reminderCalender.set(Calendar.YEAR, date.year)
                reminderCalender.set(Calendar.MONTH, date.monthValue - 1)
                reminderCalender.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)

            } else {
                val dateparts = findViewById<TextView>(R.id.editReminderDate).text.toString().split(".").toTypedArray()
                reminderCalender.set(Calendar.YEAR, dateparts[2].toInt())
                reminderCalender.set(Calendar.MONTH, dateparts[1].toInt() - 1)
                reminderCalender.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())
            }

            var status: Int
            if (reminderCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                status = 1
            }else {
                status = 0
            }
            var username: String = applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).getString("Username", "").toString()
            val reminderInfo = ReminderInfo(
                    reminderID,
                    heading = findViewById<TextView>(R.id.editReminderHeading).text.toString(),
                    message = findViewById<TextView>(R.id.editReminderText).text.toString(),
                    reminder_time = findViewById<TextView>(R.id.editReminderDate).text.toString(),
                    creation_time = System.currentTimeMillis(),
                    creator_id =  username,
                    reminder_seen = status,
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
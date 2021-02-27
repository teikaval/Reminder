package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
            //we need current date values for the notification from excercises
            val reminderCalender = GregorianCalendar.getInstance()
            val dateFormat = "dd.MM.yyyy"

            //build version chech from excercises
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

            //lets check what our reminder_seen value should be
            var status: Int
            if (reminderCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                status = 1
            }else {
                status = 0
            }
            //add values to the reminderInfo fields
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

            //asynchronous task for database update
            AsyncTask.execute {
                val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, getString(R.string.dbFileName)).build()
                val uuid = db.reminderDao().updateReminder(reminderInfo)
                db.close()

                //if reminder is in the future lets set it up
                if (reminderCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                    val message =
                            "Remember ${reminderInfo.heading} ${reminderInfo.message} is due ${reminderInfo.reminder_time}"
                    SecondScreen.setReminderWithWorkManager(
                            applicationContext,
                            reminderID,
                            reminderCalender.timeInMillis,
                            message
                    )
                }
            }

            //also give user a little toast that reminder is set
            if (reminderCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                Toast.makeText(
                        applicationContext,
                        "Reminder for future reminder saved.",
                        Toast.LENGTH_SHORT
                ).show()
            }
            finish()

            //return back to the SecondScreen
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
    }
}
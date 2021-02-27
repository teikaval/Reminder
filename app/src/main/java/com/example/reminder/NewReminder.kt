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


//This activity will handle creation of new reminders
class NewReminder : AppCompatActivity() {
    private lateinit var reminderCalender: Calendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_reminder)

        //if user cancels the creation of new reminder lets return back to the SecondsScreen
        findViewById<Button>(R.id.cancelReminderBtn).setOnClickListener {
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
        //if user presses the add button lets import new reminder to database
        findViewById<Button>(R.id.addReminderBtn).setOnClickListener {
            //here needs to be steps to save data to the room database
            val reminderCalender = GregorianCalendar.getInstance()
            val dateFormat = "dd.MM.yyyy" // change this format to dd.MM.yyyy if you have not time in your date.
            // a better way of handling dates but requires API version 26 (Build.VERSION_CODES.O)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern(dateFormat)
                val date = LocalDate.parse(findViewById<TextView>(R.id.reminderDate).text.toString(), formatter)

                reminderCalender.set(Calendar.YEAR, date.year)
                reminderCalender.set(Calendar.MONTH, date.monthValue - 1)
                reminderCalender.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)

            } else {

                val dateparts = findViewById<TextView>(R.id.reminderDate).text.toString().split(".").toTypedArray()
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
                    null,
                    heading = findViewById<TextView>(R.id.reminderHeadingtext).text.toString(),
                    message = findViewById<TextView>(R.id.reminderTexttext).text.toString(),
                    reminder_time = findViewById<TextView>(R.id.reminderDate).text.toString(),
                    creation_time = System.currentTimeMillis(),
                    creator_id =  username,
                    reminder_seen = status,
                    location_x = 0.0,
                    location_y = 0.0)


            AsyncTask.execute {
                val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        getString(R.string.dbFileName)
                ).build()
                val uuid = db.reminderDao().insert(reminderInfo).toInt()
                db.close()

                if (reminderCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                    val message =
                            "Remember ${reminderInfo.heading} ${reminderInfo.message} is due ${reminderInfo.reminder_time}"
                    SecondScreen.setReminderWithWorkManager(
                            applicationContext,
                            uuid,
                            reminderCalender.timeInMillis,
                            message
                    )
                }
            }


            if (reminderCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                Toast.makeText(
                    applicationContext,
                    "Reminder for future reminder saved.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
            //lets return back to the SecondScreen after creation has completed
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
    }
}

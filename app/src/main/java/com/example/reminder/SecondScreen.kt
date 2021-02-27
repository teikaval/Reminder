package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.reminder.databinding.ActivitySecondScreenBinding
import com.example.reminder.db.AppDatabase
import com.example.reminder.db.ReminderInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random



//screen which will show the reminders in the future using ListView

class SecondScreen : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var binding: ActivitySecondScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_second_screen)
        binding = ActivitySecondScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        listView = binding.reminderList

        refreshListView()

        //This will be addition of new reminder to be implemented
        findViewById<FloatingActionButton>(R.id.addReminderButton).setOnClickListener {
            startActivity(Intent(applicationContext, NewReminder::class.java))
        }

        //handles loggin out from software. Sets LoginStatus to 0 so that when application opens it is logged off
        //and closes the application
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE)
                .edit().putInt("LoginStatus", 0).apply()
            finishAffinity()
        }

        //check if profile button has been pressed and switches to profile view
        findViewById<Button>(R.id.ProfileButton).setOnClickListener {
            startActivity(Intent(applicationContext, ProfileView::class.java))
        }

        //Checks if item in the ListView has been pressed and switches to the editing of reminder
        listView.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, id ->
            val selectedReminder = listView.adapter.getItem(position) as ReminderInfo
            val reminderID = selectedReminder.uid
            val intent = Intent(applicationContext, EditReminderView::class.java)
            intent.putExtra("reminderID", reminderID)
            startActivity(intent)
        }

        //Checks if item has been pressed and held for a while to delete item in the ListView
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, id ->
            val selectedReminder = listView.adapter.getItem(position) as ReminderInfo
                AsyncTask.execute {
                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java,getString(R.string.dbFileName)).build()
                    db.reminderDao().delete(selectedReminder.uid!!)

                    refreshListView()
                    db.close()
                }
            true
            }
        }

         //function to refresh list when resuming (from exercises)
         override fun onResume() {
            super.onResume()
            refreshListView()
        }
    //function for refreshing listview (from exercises) and also modifies the reminder_seen item
        private fun refreshListView() {
            var refreshTask = LoadReminderEntries()
            refreshTask.execute()
        }

    //loads all reminders into list for the listview (modified from exercises) and also modifies the reminder_seen
     inner class LoadReminderEntries : AsyncTask<String?, String?, List<ReminderInfo>>() {
        override fun doInBackground(vararg params: String?): List<ReminderInfo> {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, getString(R.string.dbFileName)).build()
            val reminderInfos2 = db.reminderDao().getAllReminderInfos() //temporary value witch will hold all reminders
            for (i in reminderInfos2.indices) { //lets loop over every item
                //calendar datetime items
                val reminderCalender = GregorianCalendar.getInstance()
                val dateFormat = "dd.MM.yyyy"
                //build version check
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val formatter = DateTimeFormatter.ofPattern(dateFormat)
                    val date = LocalDate.parse(reminderInfos2[i].reminder_time.toString(), formatter)

                    reminderCalender.set(Calendar.YEAR, date.year)
                    reminderCalender.set(Calendar.MONTH, date.monthValue - 1)
                    reminderCalender.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)

                } else {

                    val dateparts = reminderInfos2[i].reminder_time.toString().split(".").toTypedArray()
                    reminderCalender.set(Calendar.YEAR, dateparts[2].toInt())
                    reminderCalender.set(Calendar.MONTH, dateparts[1].toInt() - 1)
                    reminderCalender.set(Calendar.DAY_OF_MONTH, dateparts[0].toInt())
                }
                //if reminder has happened and reminder_seen is not 0 set it to 0
                if (reminderCalender.timeInMillis < Calendar.getInstance().timeInMillis && reminderInfos2[i].reminder_seen != 0) {
                    val uuuid: Int? = reminderInfos2[i].uid
                    if (uuuid != null) {
                        db.reminderDao().updateSeen(uuuid)
                    }
                }

            }
            val reminderInfos = db.reminderDao().getReminderInfos()
            db.close()
            return reminderInfos
        }

        //if list is empty lets not show anything in the listview
        override fun onPostExecute(reminderInfos: List<ReminderInfo>?) {
            super.onPostExecute(reminderInfos)
            if (reminderInfos != null) {
                if (reminderInfos.isNotEmpty()) {
                    val adapter = ListAdapter(applicationContext, reminderInfos)
                    listView.adapter = adapter
                } else {
                    listView.adapter = null
                }
            }
        }

    }

    //more notification handlers
    companion object {

        fun showNotification(context: Context, message: String) {

            val CHANNEL_ID = "REMINDER_APP_NOTIFICATION_CHANNEL"
            var notificationId = Random.nextInt(10, 1000) + 5

            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(CHANNEL_ID)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Notification chancel needed since Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(notificationId, notificationBuilder.build())

        }

        fun setReminderWithWorkManager(
            context: Context,
            uid: Int,
            timeInMillis: Long,
            message: String
        ) {

            val reminderParameters = Data.Builder()
                .putString("message", message)
                .putInt("uid", uid)
                .build()

            // get minutes from now until reminder
            var minutesFromNow = 0L
            if (timeInMillis > System.currentTimeMillis()) //Change this arrow other way to test notifications
                minutesFromNow = timeInMillis - System.currentTimeMillis()

            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(reminderParameters)
                .setInitialDelay(minutesFromNow, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(reminderRequest)
        }
    }
}

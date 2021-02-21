package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.reminder.databinding.ActivitySecondScreenBinding
import com.example.reminder.db.AppDatabase
import com.example.reminder.db.ReminderInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton



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

        listView.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, id ->
            val selectedReminder = listView.adapter.getItem(position) as ReminderInfo
            val reminderID = selectedReminder.uid
            val intent = Intent(applicationContext, EditReminderView::class.java)
            intent.putExtra("reminderID", reminderID)
            startActivity(intent)
        }

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


         override fun onResume() {
            super.onResume()
            refreshListView()
        }

        private fun refreshListView() {
            var refreshTask = LoadReminderEntries()
            refreshTask.execute()
        }

     inner class LoadReminderEntries : AsyncTask<String?, String?, List<ReminderInfo>>() {
        override fun doInBackground(vararg params: String?): List<ReminderInfo> {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, getString(R.string.dbFileName)).build()
            val reminderInfos = db.reminderDao().getReminderInfos()
            db.close()
            return reminderInfos
        }

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

    }

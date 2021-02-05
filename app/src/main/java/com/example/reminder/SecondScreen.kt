package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

//screen which will show the reminders in the future using ListView

class SecondScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_screen)
       val listView = findViewById<ListView>(R.id.reminderList)
        val myStringArray = mutableListOf("Otsikko", "Tiedot")
        val adapter = ListAdapter(this, myStringArray)
        listView.adapter = adapter

        //This will be addition of new reminder to be implemented
        //findViewById<Button>(R.id.addReminderButton).setOnClickListener(
          //  startActivity()
        //)

        //handles loggin out from software. Sets LoginStatus to 0 so that when application opens it is logged off
        //and closes the application
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).edit().putInt("LoginStatus", 0).apply()
            finishAffinity()
        }

        //check if profile button has been pressed and switches to profile view
        findViewById<Button>(R.id.ProfileButton).setOnClickListener{
            startActivity(Intent(applicationContext, ProfileView::class.java))
        }



    }
}
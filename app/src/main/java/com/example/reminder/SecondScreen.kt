package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

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

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).edit().putInt("LoginStatus", 0).apply()
            finishAffinity()
        }

        findViewById<Button>(R.id.ProfileButton).setOnClickListener{
            startActivity(Intent(applicationContext, ProfileView::class.java))
        }



    }
}
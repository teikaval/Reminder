package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NewUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        findViewById<Button>(R.id.createNewUserBtn).setOnClickListener{
            createUser()
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

    private fun createUser() {
        val inputUsername = findViewById<TextView>(R.id.newUsername).text.toString()
        val inputPassword = findViewById<TextView>(R.id.newPassword).text.toString()
        applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).edit().putString("Username", inputUsername).apply()
        applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).edit().putString("Password", inputPassword).apply()
    }
}
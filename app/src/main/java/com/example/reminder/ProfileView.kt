package com.example.reminder

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)

        val userName = applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).getString("Username", "")
        val passWord = applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).getString("Password", "")

        findViewById<TextView>(R.id.userNameData).text = userName
        findViewById<TextView>(R.id.passwordData).text = passWord
    }
}
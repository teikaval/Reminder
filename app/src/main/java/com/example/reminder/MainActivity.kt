package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //checks if user is already logged in and if it is opens in logged in mode
        val logInStatus = applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).getInt("LoginStatus", 0).toInt()
        if (logInStatus == 1) {
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }

        //checks when login button is pressed and authenticates
        findViewById<Button>(R.id.loginbutton).setOnClickListener{
            //Log.d("Start","Log in btn clicked")

            checkLoginStatus()
        }

        //opens new user creation
        findViewById<Button>(R.id.newUser).setOnClickListener{
            startActivity(Intent(applicationContext, NewUser::class.java))
        }
    }

    //function for checking username and password
    private fun checkLoginStatus() {
        val userName = applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).getString("Username", "")
        val passWord = applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).getString("Password", "")
        val inputUsername = findViewById<TextView>(R.id.editTextTextPersonName2).text.toString()
        val inputPassword = findViewById<TextView>(R.id.editTextTextPassword2).text.toString()

        if (inputUsername == userName && inputPassword == passWord) {
            applicationContext.getSharedPreferences("com.example.reminder", Context.MODE_PRIVATE).edit().putInt("LoginStatus", 1).apply()
            startActivity(Intent(applicationContext, SecondScreen::class.java))
        }
    }
}
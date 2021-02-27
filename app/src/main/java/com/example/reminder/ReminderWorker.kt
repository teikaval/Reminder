package com.example.reminder

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(appContext:Context, workerParameters: WorkerParameters) :
        Worker(appContext,workerParameters) {

    override fun doWork(): Result {
        val text = inputData.getString("message") // this comes from the reminder parameters
        SecondScreen.showNotification(applicationContext,text!!)
        return   Result.success()
    }
}
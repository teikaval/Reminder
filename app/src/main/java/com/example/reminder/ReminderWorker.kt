package com.example.reminder

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
//from exercises handler for notifications
class ReminderWorker(appContext:Context, workerParameters: WorkerParameters) :
        Worker(appContext,workerParameters) {

    override fun doWork(): Result {
        val text = inputData.getString("message") // this comes from the reminder
        SecondScreen.showNotification(applicationContext,text!!)
        return   Result.success()
    }
}
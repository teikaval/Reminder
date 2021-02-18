package com.example.reminder.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ReminderInfo::class), version = 1)
abstract class AppDatabase:RoomDatabase() {
   abstract fun reminderDao():ReminderDao
}
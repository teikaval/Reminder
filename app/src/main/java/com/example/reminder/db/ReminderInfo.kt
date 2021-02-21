package com.example.reminder.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//database table modified from exercises
@Entity(tableName = "reminderInfo")
data class ReminderInfo(
        @PrimaryKey(autoGenerate = true) var uid: Int?,
        @ColumnInfo(name = "heading") var heading: String,
        @ColumnInfo(name = "message") var message: String,
        @ColumnInfo(name = "reminder_time") var reminder_time: String,
        @ColumnInfo(name = "creation_time") var creation_time: String,
        @ColumnInfo(name = "location_x") var location_x: Double,
        @ColumnInfo(name = "location_y") var location_y: Double,
        @ColumnInfo(name = "creator_id") var creator_id: String,
        @ColumnInfo(name = "reminder_seen") var reminder_seen: Int?,
)
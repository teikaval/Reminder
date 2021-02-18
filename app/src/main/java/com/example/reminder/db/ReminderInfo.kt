package com.example.reminder.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "reminderInfo")
data class ReminderInfo(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name="heading") var heading:String,
    @ColumnInfo(name="text")  var text:String,
    @ColumnInfo(name="date") var date:String,
    @ColumnInfo(name="amount") var amount: String
)
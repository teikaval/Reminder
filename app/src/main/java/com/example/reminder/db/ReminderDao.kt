package com.example.reminder.db

import androidx.room.*
//database object modified from exercises

@Dao
interface ReminderDao {
    @Transaction
    @Insert
    fun insert(reminderInfo: ReminderInfo): Long

    @Query("DELETE FROM reminderInfo WHERE uid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM reminderInfo")
    fun getAllReminderInfos(): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE reminder_seen = 0")
    fun getReminderInfos(): List<ReminderInfo>

    //handles updating of the database
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateReminder(reminderInfo: ReminderInfo)

    //Updates the reminder seen so that the reminder will be shown in the list
    @Query("UPDATE reminderInfo SET reminder_seen = 0 WHERE uid = :id ")
    fun updateSeen(id: Int)
}
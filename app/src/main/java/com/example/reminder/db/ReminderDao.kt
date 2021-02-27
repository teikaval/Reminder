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

    //gets all reminderitems for the reminder_seen change
    @Query("SELECT * FROM reminderInfo")
    fun getAllReminderInfos(): List<ReminderInfo>

    //put in the listview only those items that are in the future
    @Query("SELECT * FROM reminderInfo WHERE reminder_seen = 0")
    fun getReminderInfos(): List<ReminderInfo>

    //handles updating of the database
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateReminder(reminderInfo: ReminderInfo)

    //Updates the reminder seen so that the reminder will be shown in the list
    @Query("UPDATE reminderInfo SET reminder_seen = 0 WHERE uid = :id ")
    fun updateSeen(id: Int)
}
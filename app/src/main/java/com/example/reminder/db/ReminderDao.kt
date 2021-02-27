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
    fun getReminderInfos(): List<ReminderInfo>

    //handles updating of the database
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateReminder(reminderInfo: ReminderInfo)
}
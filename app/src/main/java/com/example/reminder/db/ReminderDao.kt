package com.example.reminder.db

import androidx.room.*

@Dao
interface ReminderDao {
    @Transaction
    @Insert
    fun insert(reminderInfo: ReminderInfo): Long

    @Query("DELETE FROM reminderInfo WHERE uid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM reminderInfo")
    fun getReminderInfos(): List<ReminderInfo>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateReminder(reminderInfo: ReminderInfo)
}
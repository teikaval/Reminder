package com.example.reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.reminder.databinding.ReminderitemBinding
import com.example.reminder.db.ReminderInfo


//Handles the ListView for the SecondScreen which holds the reminders modified from exercises
class ListAdapter (context: Context, private val list: List<ReminderInfo>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    //List of items that are to be shown in the listview
    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        val rowBinding = ReminderitemBinding.inflate(inflater, container, false)
        rowBinding.reminderheading.text = list[position].heading
        rowBinding.remindertext.text = list[position].message
        rowBinding.reminderDateShow.text = list[position].reminder_time
        rowBinding.creatorDataText.text = list[position].creator_id

        return rowBinding.root
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}
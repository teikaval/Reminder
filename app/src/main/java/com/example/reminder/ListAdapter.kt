package com.example.reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

val myStringArray = mutableListOf<String>("Teemu")
//Handles the ListView for the SecondScreen which holds the reminders
class ListAdapter (private val context: Context,
private val list: MutableList<String> = myStringArray) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.reminderitem, parent, false)
        val headingTextView = rowView.findViewById(R.id.reminderheading) as TextView
        val detailTextView = rowView.findViewById(R.id.remindertext) as TextView

        //val reminder = getItem(position) as Reminder
        headingTextView.text = list[0]
        detailTextView.text = list[1]

        return rowView
    }
}
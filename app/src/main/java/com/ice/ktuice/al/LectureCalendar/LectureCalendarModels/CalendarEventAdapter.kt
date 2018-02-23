package com.ice.ktuice.al.LectureCalendar.LectureCalendarModels

import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ice.ktuice.R
import io.realm.RealmQuery
import io.realm.RealmResults

/**
 * Created by Andrius on 2/23/2018.
 * TODO pass items as realm results
 */
class CalendarEventAdapter(private val context: Context, private val items: List<CalendarEvent>): BaseAdapter() {
    init{
        //items.sort("dateStart")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.list_item_calendar_event, parent, false)
        val item = items[position]
        val summaryText = view.findViewById<TextView>(R.id.event_summary)
        val descriptionText = view.findViewById<TextView>(R.id.event_location)
        val startDateText = view.findViewById<TextView>(R.id.event_start)

        summaryText.text = item.summary
        descriptionText.text = item.getLocationString()
        startDateText.text = item.getStartTimeString()

        view.setBackgroundResource(R.drawable.calendar_event_item_background)
        val bgDrawable = view.background.mutate()
        DrawableCompat.setTint(bgDrawable, item.getCategoryColor(context))
        return  view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = items.size
}
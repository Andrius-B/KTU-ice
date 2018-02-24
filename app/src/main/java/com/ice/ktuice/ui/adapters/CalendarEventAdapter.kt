package com.ice.ktuice.ui.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TableRow
import android.widget.TextView
import com.ice.ktuice.R
import com.ice.ktuice.al.LectureCalendar.LectureCalendarModels.CalendarEvent
import java.security.spec.ECField

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
        val item = getItem(position)

        val summaryRow = view.findViewById<TableRow>(R.id.event_summary_row)
        val locationRow = view.findViewById<TableRow>(R.id.event_location_row)
        val startTimeRow = view.findViewById<TableRow>(R.id.event_start_time_row)
        val summaryText = view.findViewById<TextView>(R.id.event_summary)
        val descriptionText = view.findViewById<TextView>(R.id.event_location)
        val startDateText = view.findViewById<TextView>(R.id.event_start)

        if(item != null) {
            summaryText.text = item.summary
            descriptionText.text = item.getLocationString()
            startDateText.text = item.getStartTimeString()

            summaryRow.visibility = View.VISIBLE
            locationRow.visibility = View.VISIBLE
            startTimeRow.visibility = View.VISIBLE
            view.setBackgroundResource(R.drawable.calendar_event_item_background)
            val bgDrawable = view.background.mutate()
            DrawableCompat.setTint(bgDrawable, item.getCategoryColor(context))
        }else{
            summaryRow.visibility = View.GONE
            locationRow.visibility = View.GONE
            startTimeRow.visibility = View.GONE
            view.setBackgroundResource(R.drawable.calendar_event_item_background)
            val bgDrawable = view.background.mutate()
            DrawableCompat.setTint(bgDrawable, ContextCompat.getColor(context, R.color.transparent))
        }
        return  view
    }

    override fun getItem(position: Int): CalendarEvent? {
        return try{
                    items[position]
                }catch (e:Exception){
                    null
                } // try-get

    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = Int.MAX_VALUE
}
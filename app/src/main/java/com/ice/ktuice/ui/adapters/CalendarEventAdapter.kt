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
import com.ice.ktuice.al.lectureCalendar.CalendarListItemModel
import com.ice.ktuice.al.lectureCalendar.CalendarListItemModel.ItemType.*
import java.lang.Math.abs
import java.util.*

/**
 * Created by Andrius on 2/23/2018.
 * TODO pass itemModels as realm results
 */
class CalendarEventAdapter(private val context: Context, private val itemModels: List<CalendarListItemModel>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.list_item_calendar_event, parent, false)
        val item = getItem(position)

        /**
         * These are here, because they're slightly faster then the synthetic variants
         * and also, because, I learned how to use the synthetic variants too late :-/
         */
        val summaryRow = view.findViewById<TableRow>(R.id.event_summary_row)
        val locationRow = view.findViewById<TableRow>(R.id.event_location_row)
        val summaryText = view.findViewById<TextView>(R.id.event_summary)
        val descriptionText = view.findViewById<TextView>(R.id.event_location)
        val startDateText = view.findViewById<TextView>(R.id.event_start)
        val endDateText = view.findViewById<TextView>(R.id.event_end)
        val headerText = view.findViewById<TextView>(R.id.event_header_text)


        if(item != null) {
            when(item.type){
                Event -> {
                    summaryText.text = item.summary
                    descriptionText.text = item.getLocationString()
                    startDateText.text = item.getStartTimeString()
                    endDateText.text = item.getEndTimeString()

                    summaryRow.visibility = View.VISIBLE
                    startDateText.visibility = View.VISIBLE
                    endDateText.visibility = View.VISIBLE
                    locationRow.visibility = View.VISIBLE
                    headerText.visibility = View.GONE

                    view.setBackgroundResource(R.drawable.calendar_event_item_background)
                    val bgDrawable = view.background.mutate()
                    DrawableCompat.setTint(bgDrawable, item.getCategoryColor(context))
                }
                Header -> {
                    headerText.text = item.text

                    summaryRow.visibility = View.GONE
                    locationRow.visibility = View.GONE
                    startDateText.visibility = View.GONE
                    endDateText.visibility = View.GONE
                    headerText.visibility = View.VISIBLE

                    view.setBackgroundResource(R.drawable.calendar_event_item_background)
                    val bgDrawable = view.background.mutate()
                    DrawableCompat.setTint(bgDrawable, ContextCompat.getColor(context, R.color.transparent))
                }
                Break ->{
                    //TODO
                    summaryRow.visibility = View.GONE
                    locationRow.visibility = View.GONE
                    startDateText.visibility = View.VISIBLE
                    endDateText.visibility = View.VISIBLE
                    headerText.visibility = View.GONE

                    startDateText.text = item.getStartTimeString()
                    endDateText.text = item.getEndTimeString()

                    view.setBackgroundResource(R.drawable.calendar_event_item_background)
                    val bgDrawable = view.background.mutate()
                    DrawableCompat.setTint(bgDrawable, ContextCompat.getColor(context, R.color.event_brake_color))
                }
                Dummy ->{
                    //TODO
                    summaryRow.visibility = View.GONE
                    locationRow.visibility = View.GONE
                    startDateText.visibility = View.GONE
                    endDateText.visibility = View.GONE
                    headerText.visibility = View.GONE
                }
            }

        }else{
            summaryRow.visibility = View.GONE
            locationRow.visibility = View.GONE
            startDateText.visibility = View.GONE
            endDateText.visibility = View.GONE

            view.setBackgroundResource(R.drawable.calendar_event_item_background)
            val bgDrawable = view.background.mutate()
            DrawableCompat.setTint(bgDrawable, ContextCompat.getColor(context, R.color.transparent))
        }
        return  view
    }

    /**
     * Finds a header element, that has a start date as close as possible to the specified date
     * returns the position of the found element
     */
    fun getPositionFromDate(year: Int, month: Int, dayOfMonth: Int): Int{
        val cal = Calendar.getInstance()
        cal.set(year, month, dayOfMonth, 0, 0, 0)

        var minTimeDiff = Long.MAX_VALUE
        var index = -1
        itemModels.forEachIndexed{
            i, it ->
            try {
                val eventCal = Calendar.getInstance()
                eventCal.time = it.dateStart
                val timeDiff = abs(eventCal.timeInMillis - cal.timeInMillis)
                if(timeDiff < minTimeDiff){
                    index = i
                    minTimeDiff = timeDiff
                }
            }catch (e: Exception){ ; } //in case the adapter item event is not present
        }
        return index
    }

    override fun getItem(position: Int): CalendarListItemModel? {
        return try{
                    itemModels[position]
                }catch (e:Exception){
                    null
                } // try-get

    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = itemModels.size
}
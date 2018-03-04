package com.ice.ktuice.ui.main.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekViewEvent
import com.ice.ktuice.R
import com.ice.ktuice.al.LectureCalendar.CalendarListItemModel
import com.ice.ktuice.ui.adapters.CalendarEventAdapter
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.scraperService.handlers.CalendarHandler
import kotlinx.android.synthetic.main.fragment_timetable.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.text.DateFormat
import java.util.*
import com.ice.ktuice.al.LectureCalendar.CalendarListItemModel.ItemType.*
import com.ice.ktuice.al.LectureCalendar.CalendarManager
import java.lang.Math.abs

/**
 * Created by Andrius on 2/24/2018.
 */
class FragmentTimeTable: Fragment(), KoinComponent {
    //private var calendarModel: CalendarModel = CalendarModel()
    private val userService: UserService by inject()

    private val calendarManager = CalendarManager()

    private val events = mutableListOf<WeekViewEvent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_timetable, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set an action when any event is clicked.
        week_view.setOnEventClickListener{event, eventRect ->  println("Click on event!")}

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        week_view.setMonthChangeListener { newYear, newMonth ->
            run{
                println("set date set:$newYear $newMonth")
            }
            events
        }

        // Set long press listener for events.
        week_view.setEventLongPressListener{event, eventRect ->  println("Long click on event!")}

        //this is what the developers of the lib use, but i think its a little non responsive if scrolling slowly
        //week_view.xScrollingSpeed = -1 * (Math.log(2.5) / Math.log(1.0 / (1 + week_view.numberOfVisibleDays))).toFloat()


        /**
         * Download the calendar data and update the adapter
         */
        doAsync ({
            println(it)
        },{

            val eventsTemp = calendarManager.getCalendarEventsModelFromWeb(userService.getLoginForCurrentUser()!!)
            events.clear()
            events.addAll(eventsTemp)
            uiThread {
                println("Calendar downloaded!")
                week_view.notifyDatasetChanged()
            }
        })
    }

    private fun setViewForDate(year: Int, month:Int, dayOfMonth:Int, adapter: CalendarEventAdapter){
        val targetPos = adapter.getPositionFromDate(year,month, dayOfMonth)
    }
}
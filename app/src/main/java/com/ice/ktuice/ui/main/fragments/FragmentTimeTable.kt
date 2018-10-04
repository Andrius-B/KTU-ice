package com.ice.ktuice.ui.main.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alamkanak.weekview.WeekViewEvent
import com.ice.ktuice.R
import com.ice.ktuice.al.lectureCalendar.CalendarManager
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.fragment_timetable.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.koin.standalone.KoinComponent

/**
 * Created by Andrius on 2/24/2018.
 * Displays a calendar of the upcoming events
 */
class FragmentTimeTable: Fragment(), KoinComponent, AnkoLogger{

    private val calendarManager = CalendarManager()

    private val events = mutableListOf<WeekViewEvent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set an action when any event is clicked.
        week_view.setOnEventClickListener{ _, _ ->  info("Click on event!")}

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        week_view.setMonthChangeListener { newYear, newMonth ->
            run{
                info("set date set:$newYear $newMonth")
            }
            events
        }

        // Set long press listener for events.
        week_view.setEventLongPressListener{ _, _ ->  info("Long click on event!")}

        //this is what the developers of the lib use, but i think its a little non responsive if scrolling slowly
        week_view.xScrollingSpeed = -1 * (Math.log(2.5) / Math.log(1.0 / (1 + week_view.numberOfVisibleDays))).toFloat()


        /**
         * Display all the upcoming events
         */
        val calendarSubject = calendarManager.getCalendarModel()
        calendarSubject.subscribe{
            updateWeekViewToCalendar(it)
        }
        goto_today_btn.setOnClickListener {
            week_view.goToToday()
        }
    }


    private fun updateWeekViewToCalendar(calendar: CalendarModel){
        info("Updating the calendar view!")
        val eventList = weekViewEventsFromCalendar(calendar)
        events.clear()
        events.addAll(eventList)
        week_view.notifyDatasetChanged()
    }

    private fun weekViewEventsFromCalendar(calendar: CalendarModel): List<WeekViewEvent>{
        val list = mutableListOf<WeekViewEvent>()
        calendar.eventList.forEach {
            val event = WeekViewEvent()
            event.startTime = CalendarManager.convertDateToCalendar(it.dateStart)
            event.endTime = CalendarManager.convertDateToCalendar(it.dateEnd)
            event.color = it.getCategoryColor(this.activity!!)
            event.name = it.summary
            event.location = it.getLocationString()
            list.add(event)
        }
        return list
    }
}
package com.ice.ktuice.ui.main.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ice.ktuice.R
import com.ice.ktuice.al.GradeTable.GradeTableManager
import com.ice.ktuice.ui.adapters.CalendarEventAdapter
import com.ice.ktuice.al.LectureCalendar.LectureCalendarModels.CalendarModel
import com.ice.ktuice.scraperService.handlers.CalendarHandler
import kotlinx.android.synthetic.main.fragment_timetable.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Andrius on 2/24/2018.
 */
class FragmentTimeTable: Fragment() {
    private var calendarModel: CalendarModel = CalendarModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Creating TimeTable fragment!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_timetable, container, false)
        val tableManager = GradeTableManager()
        doAsync ({
            println(it)
        },{
            calendarModel = CalendarHandler.getCalendar(tableManager.getLoginForCurrentUser())
            val now = Calendar.getInstance()
            uiThread {
                setViewForDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                calendar_view.setOnDateChangeListener {
                    view, year, month, dayOfMonth ->
                    setViewForDate(year, month, dayOfMonth)
                }
            }
        })
        return view
    }

    private fun setViewForDate(year: Int, month:Int, dayOfMonth:Int){
        val beforeDate = Calendar.getInstance()
        val afterDate = Calendar.getInstance()
        beforeDate.set(year, month, dayOfMonth, 0, 0, 0)
        afterDate.set(year, month, dayOfMonth, 23, 59, 59)
        val items = calendarModel.eventList.filter{
            it.dateStart.after(beforeDate.time) && it.dateEnd.before(afterDate.time)
        }.sortedBy { it.dateStart }

        agenda_items.adapter = CalendarEventAdapter(this.activity!!, items)
    }
}
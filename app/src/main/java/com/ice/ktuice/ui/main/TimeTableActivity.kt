package com.ice.ktuice.ui.main

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.ice.ktuice.R
import com.ice.ktuice.al.GradeTable.GradeTableManager
import com.ice.ktuice.al.LectureCalendar.LectureCalendarModels.CalendarEventAdapter
import com.ice.ktuice.al.LectureCalendar.LectureCalendarModels.CalendarModel
import com.ice.ktuice.scraperService.handlers.CalendarHandler
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_timetable.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Andrius on 2/23/2018.
 */
class TimeTableActivity: AppCompatActivity() {
    private var calendarModel: CalendarModel = CalendarModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)
        println("Timetable activity view created!")

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

    }

    private fun setViewForDate(year: Int, month:Int, dayOfMonth:Int){
        val beforeDate = Calendar.getInstance()
        val afterDate = Calendar.getInstance()
        beforeDate.set(year, month, dayOfMonth, 0, 0, 0)
        afterDate.set(year, month, dayOfMonth, 23, 59, 59)
        val items = calendarModel.eventList.filter{
            it.dateStart.after(beforeDate.time) && it.dateEnd.before(afterDate.time)
        }.sortedBy { it.dateStart }

        agenda_items.adapter = CalendarEventAdapter(this, items)
    }
}
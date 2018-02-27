package com.ice.ktuice.ui.main.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    /**
     * On inflation the calendar is shown, but is moved (via setX()) as soon as possible
     */
    private var _calShowing = false
    var isCalendarShowing: Boolean
        get(){
            return _calShowing
        }
        set(value){
            val offset = if(value){
                -calendar_view.width.toFloat()
            }else{
                calendar_view.width.toFloat()
            }
            calendar_view.postDelayed({
                calendar_container.animate().xBy(offset)
                _calShowing = value
            }, 5)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_timetable, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        agenda_items.isVerticalScrollBarEnabled = false
        agenda_items.isHorizontalScrollBarEnabled = false

        /**
         * Download the calendar data and update the adapter
         */
        doAsync ({
            println(it)
        },{
            val eventList = calendarManager.getCalendarEventsModelFromWeb(userService.getLoginForCurrentUser()!!)
            val now = Calendar.getInstance()
            uiThread {
                val tableAdapter = CalendarEventAdapter(it.activity!!, eventList)
                agenda_items.adapter = tableAdapter
                setViewForDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), tableAdapter)
                calendar_view.setOnDateChangeListener {
                    _, year, month, dayOfMonth ->
                    setViewForDate(year, month, dayOfMonth, tableAdapter)
                }
            }
        })

        /**
         * Toggle the calendar on button click
         */
        calendar_toggle_button.setOnClickListener {
            isCalendarShowing = !isCalendarShowing
        }

        /**
         * Hide the calendar initially
         */
        calendar_view.post{
            calendar_container.x = calendar_view.measuredWidth.toFloat()
        }
    }

    private fun setViewForDate(year: Int, month:Int, dayOfMonth:Int, adapter: CalendarEventAdapter){
        val targetPos = adapter.getPositionFromDate(year,month, dayOfMonth)
        agenda_items.smoothScrollToPosition(targetPos)
        /* TODO scroll instantly if the delta scroll is too large
        val currentPos = agenda_items.selectedItemPosition
        if(abs(targetPos - currentPos) <= 20){
            agenda_items.smoothScrollToPosition(targetPos)
        }else{
            agenda_items.setSelection(targetPos)
        }*/

    }
}
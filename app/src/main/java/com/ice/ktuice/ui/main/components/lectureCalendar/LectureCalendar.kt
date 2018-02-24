package com.ice.ktuice.ui.main.components.lectureCalendar

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.ice.ktuice.R
import com.ice.ktuice.al.GradeTable.GradeTableManager
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.scraperService.handlers.CalendarHandler
import org.jetbrains.anko.doAsync
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/23/2018.
 */
class LectureCalendar(context:Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet), KoinComponent{
    private val userService: UserService by inject()
    init{
        inflate(context, R.layout.fragment_timetable, this)
        doAsync({
            println(it)
        },{
            val login = userService.getLoginForCurrentUser()!!
            val cal = CalendarHandler.getCalendar(login)
            println(cal)
        })
    }

}
package com.ice.ktuice.al.LectureCalendar

import android.content.Context
import android.graphics.Color
import com.alamkanak.weekview.WeekViewEvent
import com.ice.ktuice.DAL.repositories.calendarRepository.CalendarRepository
import com.ice.ktuice.DAL.repositories.calendarRepository.CalendarRepositoryImpl
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.lectureCalendarModels.CalendarEvent
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import com.ice.ktuice.scraperService.handlers.CalendarHandler
import com.ice.ktuice.al.LectureCalendar.CalendarListItemModel.ItemType.*
import com.ice.ktuice.al.services.userService.UserService
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.uiThread
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.text.DateFormat
import java.util.*


/**
 * Created by Andrius on 2/26/2018.
 */
class CalendarManager() : KoinComponent {
    private val calendarRepository = CalendarRepositoryImpl()
    private val userService: UserService by inject()

    private val headerDateFormat = DateFormat.getDateInstance()

    fun getCalendarEventsModelFromWeb(context: Context): CalendarModel{
        val login = userService.getLoginForCurrentUser()!!
        val calendar = CalendarHandler.getCalendar(login)
        return calendar
    }

    fun getCalendarModel(context: Context): Subject<CalendarModel>{
        val subject: ReplaySubject<CalendarModel> = ReplaySubject.create(2)
        val login = userService.getLoginForCurrentUser()!!
        var calendar = calendarRepository.getByStudCode(login.studentId)

        if(calendar == null ){
            calendar = CalendarModel()
            calendar.studCode = login.studentId
            calendarRepository.createOrUpdate(calendar)
        }
        subject.onNext(calendar)

        doAsync (
        {
            println(it.getStackTraceString())
        },
        {
            val freshCalendar = getCalendarEventsModelFromWeb(context)
            uiThread {
                calendarRepository.createOrUpdate(freshCalendar)
                subject.onNext(freshCalendar)
            }
        })
        return subject
    }


    companion object {
        fun convertDateToCalendar(d: Date): Calendar{
            val cal = Calendar.getInstance()
            cal.time = d
            return cal
        }
    }
}
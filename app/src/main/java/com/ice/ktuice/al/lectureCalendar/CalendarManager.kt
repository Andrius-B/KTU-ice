package com.ice.ktuice.al.lectureCalendar

import com.ice.ktuice.repositories.calendarRepository.CalendarRepositoryImpl
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import com.ice.ktuice.al.services.scrapers.calendar.CalendarScraper
import com.ice.ktuice.al.services.userService.UserService
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import org.jetbrains.anko.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*


/**
 * Created by Andrius on 2/26/2018.
 * The main logic behind the calendar view
 */
class CalendarManager: KoinComponent, AnkoLogger {
    private val calendarRepository = CalendarRepositoryImpl()
    private val userService: UserService by inject()
    private val calendarScraper: CalendarScraper by inject()

    private fun getCalendarEventsModelFromWeb(): CalendarModel{
        val login = userService.getLoginForCurrentUser()!!
        val calendar = calendarScraper.getCalendar(login)
        return calendar
    }

    fun getCalendarModel(): Subject<CalendarModel>{
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
            info(it.getStackTraceString())
        },
        {
            val freshCalendar = getCalendarEventsModelFromWeb()
            uiThread {
                calendarRepository.createOrUpdate(freshCalendar)
                try {
                    /**
                     * The subject does not guarantee that a listener is present
                     * and thus if the activity is disposed of,
                     * an exception is thrown here
                     */
                    subject.onNext(freshCalendar)
                }catch (e: Exception){
                    info(e.getStackTraceString())
                }
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
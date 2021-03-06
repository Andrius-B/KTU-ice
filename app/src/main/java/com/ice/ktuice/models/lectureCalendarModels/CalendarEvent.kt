package com.ice.ktuice.models.lectureCalendarModels

import android.content.Context
import com.ice.ktuice.R
import io.realm.RealmObject
import io.realm.annotations.Ignore
import java.text.DateFormat
import java.util.*
import android.util.TypedValue
import androidx.core.content.ContextCompat


/**
 * Created by Andrius on 2/23/2018.
 * Class to store information about an event (mostly lectures here)
 */
open class CalendarEvent: RealmObject(){
    open var summary: String = ""
    open var description: String = ""
    open var dateStamp: Date = Date()
    open var dateStart: Date = Date()
    open var dateEnd: Date = Date()
    open var location: String = ""
    open var categories: String = ""


    /**
     * Caching the DateFormat
     */
    @Ignore
    private val dateFormat = DateFormat.getTimeInstance()

    fun getStartTimeString() = dateFormat.format(dateStart)!!
    fun getEndTimeString() = dateFormat.format(dateEnd)!!

    /**
     * Most of the time, location consists of multiple definitions separated by a semicolon,
     * the last one of them being a long city address. Since most people know the addresses of the buildings by heart,
     * it makes sense ti remove them for being displayed, to reduce clutter
     */
    fun getLocationString(): String {
        val elements = location.split(';')
        if(elements.last().length >= 30){ // any single element longer than 30 chars is most likely too long
            return elements.subList(0, elements.size - 1).reduce{s1,s2 -> s1 + s2}
        }
        return location
    }

    private fun resolveColorAttr(c: Context, resId: Int): Int{
        val typedValue = TypedValue()
        c.theme.resolveAttribute(resId, typedValue, true)
        return typedValue.data
    }

    fun getCategoryColor(c: Context): Int {
        return when(categories){
            "Red Category"    ->    resolveColorAttr(c, R.attr.color_event_red_category)
            "Blue Category"   ->    resolveColorAttr(c, R.attr.color_event_blue_category)
            "Green Category"  ->    resolveColorAttr(c, R.attr.color_event_green_category)
            "Yellow Category" ->    resolveColorAttr(c, R.attr.color_event_yellow_category) // exams
            else              ->    ContextCompat.getColor(c, R.color.transparent)
        }
    }

    override fun toString(): String {
        return String.format("Summary:%s Date start:%s", summary, getStartTimeString())
    }
}
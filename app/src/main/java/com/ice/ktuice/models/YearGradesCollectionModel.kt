package com.ice.ktuice.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Andrius on 2/24/2018.
 * A list of year models associated with a particular student
 */
open class YearGradesCollectionModel(): RealmObject(){
    constructor(studentCode: String): this() {
        studentId = studentCode
    }

    @PrimaryKey
    var studentId: String? = ""


    open var yearList = RealmList<YearGradesModel>()
    /**
     * Tells when the object was created and also when
     */
    open var dateUpdated = Date()

    /**
     * The syncing flag between the view and the background
     * service, that updates this collection
     */
    open var isUpdating = false

    /**
     * To make notification checking a bit easier this string should contain
     * the relevant parts of the page
     *  * Note: it does not make sense to store it after initial hashing
     */
    open var rawHtml = ""

    open var htmlHash = ""

    /**
     * Model validation information
     */
    open var markCnt = 0
    open var moduleCnt = 0
    open var semesterCnt = 0
    open var yearCnt = 0

    /**
     * List interface delegation to the yearList variable
     * This is to overcome realms limitation of supporting polymorphism
     */
    fun get(index: Int) = yearList[index]!!
    fun addAll(l: List<YearGradesModel>){ yearList.addAll(l) }
    fun add(e: YearGradesModel){ yearList.add(e) }
    val size: Int
        get() = yearList.size

    fun contains(element: YearGradesModel): Boolean {
        return yearList.contains(element)
    }

    fun containsAll(elements: Collection<YearGradesModel>): Boolean {
        return yearList.containsAll(elements)
    }

    fun indexOf(element: YearGradesModel): Int {
        return yearList.indexOf(element)
    }

    fun isEmpty(): Boolean {
        return yearList.isEmpty()
    }

    fun forEach(action: (YearGradesModel) -> Unit) {
        yearList.forEach(action)
    }

    fun find (action: (YearGradesModel) -> Boolean): YearGradesModel? {
        return yearList.find(action)
    }
}
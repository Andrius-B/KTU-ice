package com.ice.ktuice.DB.entities

import com.ice.ktuice.scraper.models.LoginModel
import com.ice.ktuice.scraper.models.YearModel
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.time.Year
import java.util.*


/**
 * Created by simonas on 4/30/17.
 */

@RealmClass
open class RlUserModel : RealmObject() {

    companion object {

        /**
         * Type conversion from
         * @param model LoginModel? to RlUserModel
         */
        fun from(model: LoginModel?): RlUserModel? {
                val returnValue: RlUserModel?
                if (model == null) {
                    println("model is null!")
                    returnValue = null
                }
                else {
                    println("Creating default ret values")
                    returnValue = RlUserModel()
                    println("default value created!")
                    returnValue.cookies.addAll(createRlCookieList(model.authCookies))
                    println("stud cookies assigned")
                    returnValue.studId = model.studentId
                    println("stud id assigned")
                    returnValue.studName = model.studentName
                    println("stud name assigned")
                    //currentWeek = model.currentWeek
                    returnValue.yearList.addAll(RlYearModel.from(model.studentSemesters))
                    println("default value created!")
                }
                return returnValue
        }

        /**
         * Type Conversion from
         * @param rlu RlUserModel to LoginModel?
         */
        fun toLoginModel(rlu: RlUserModel):LoginModel?
        {
            val yearList = mutableListOf<YearModel>()
            rlu.yearList.forEach({
                if(it?.id != null && it.year != null)
                    yearList.add(YearModel(it.id!!, it.year!!))
            })
            if(remapCookies(rlu.cookies).isEmpty()) println("cookies remapped are null!")
            if(rlu.studName == null) println("studName is null!")
            if(rlu.studId == null) println("studId is null!")
            if(yearList.isEmpty()) println("yearlist is of size 0!")

            return LoginModel(remapCookies(rlu.cookies), rlu.studName!!, rlu.studId!!, yearList)
        }

        /**
         * Utility function to change types:
         * @param cookies - cookie map from jsoup request
         * @return List of Realm objects containing keys and values
         */
        private fun createRlCookieList(cookies: Map<String, String>): RealmList<RlCookie>{ //remap the map of cookies to a list of key-value pairs
            return RealmList<RlCookie>().apply {
                cookies.forEach({add(RlCookie(it.key, it.value))})
            }
        }

        /**
         * Utility function to change types:
         * @param rlCookies - cookie list from realm
         * @return map of <String, String> pairs as per standard
         */
        private fun remapCookies(rlCookies: List<RlCookie>):Map<String, String>{
            return mutableMapOf<String, String>().apply {
                rlCookies.forEach({ put(it.key, it.content) })
            }
        }

    }

    @PrimaryKey // rl
    var id = "one_id_to_rule_them_all"
    //open var currentWeek: String? = null
    var cookies: RealmList<RlCookie> = RealmList()
    var studId: String? = null
    var studName: String? = null
    var yearList: RealmList<RlYearModel> = RealmList()
    //open var weekList: RealmList<RlWeekModel> = RealmList()
    //open var gradeList: RealmList<RlGradesResponse> = RealmList()
    //var username: String? = null
    //var password: String? = null
    //open var timestamp: Long = 0L
    //open var defaultSemesterDataString: String? = null

    /*var defaultSemester: RlSemesterInfoModel
        set(value) {
            defaultSemesterDataString = value.toDataString()
        }
        get() = RlSemesterInfoModel.fromString(defaultSemesterDataString!!)*/

    fun set(rlu: RlUserModel){
        this.cookies.addAll(rlu.cookies)
        this.studId = rlu.studId
        this.studName = rlu.studName
        this.yearList.addAll(rlu.yearList)
        println("Cookie count set:"+this.cookies.size)
    }
}
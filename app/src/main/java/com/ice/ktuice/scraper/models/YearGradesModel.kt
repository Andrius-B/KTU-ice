package com.ice.ktuice.scraper.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by Andrius on 2/11/2018.
 * The final model that is stored from a request to the server
 */
@RealmClass
open class YearGradesModel(
        year: YearModel = YearModel(),
        var studId: String = "",
        var dateStamp: String = "", // when was this model fetched from the server
        var loginModel: LoginModel? = LoginModel(),
        var semesterList: RealmList<SemesterModel> = RealmList()
): RealmObject(){
        /**
         * Realm can guarantee that an object will be here,
         * but in code, the YearGradesModel can not exist without a
         * YearModel, thus this is a bypass to keep kotlin inspections clean
         */
        private var _year: YearModel? = year

        val year: YearModel
                get() = _year!!
}
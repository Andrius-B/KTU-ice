package com.ice.ktuice.DB.entities

import com.ice.ktuice.scraper.models.YearModel
import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by simonas on 4/30/17.
 */

@RealmClass
open class RlYearModel : RealmObject() {

    open var id: String? = null
    open var year: String? = null

    companion object {

        fun from(model: YearModel): RlYearModel {
            return RlYearModel().apply {
                id = model.id
                year = model.year
            }
        }

        fun from(modelList: List<YearModel>): List<RlYearModel> {
            return mutableListOf<RlYearModel>().apply {
                modelList.forEach { add(RlYearModel.from(it))}
            }
        }

    }
}
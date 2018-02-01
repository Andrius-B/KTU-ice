package com.ice.ktuice.DB.entities

import com.ice.ktuice.scraper.models.ResponseMetadataModel
import com.ice.ktuice.scraper.models.YearModel
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.RealmClass
import org.jetbrains.annotations.NotNull

/**
 * Created by simonas on 4/30/17.
 */

@RealmClass
open class RlYearModel() : RealmObject() {
    @NotNull
    open var id: String = ""
    @NotNull
    open var year: String = ""

    @LinkingObjects("yearModel")
    open val responseMetadataModels: RealmResults<RlResponseMetadata?>? = null

    constructor(id: String, year: String):this(){
        this.id = id
        this.year = year
    }
    constructor(@NotNull model: YearModel):this(model.id, model.year)


    fun toYearModel() = YearModel(id, year)
    companion object {
        fun from(modelList: List<YearModel>): List<RlYearModel> {
            return mutableListOf<RlYearModel>().apply {
                modelList.forEach { add(RlYearModel(it))}
            }
        }

    }
}
package com.ice.ktuice.DB.entities

import com.ice.ktuice.scraper.models.ResponseMetadataModel
import com.ice.ktuice.scraper.models.YearModel
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import org.jetbrains.annotations.NotNull
import java.util.*


open class RlResponseMetadata(
        @NotNull
        var studentCode: String = "",
        @NotNull
        var yearModel: RlYearModel? = RlYearModel("", ""),
        @NotNull
        var dateOfResponse: Date = Date()
): RealmObject(){
    constructor(
            studentCode: String,
            yearModel: YearModel,
            dateOfResponse: Date
            )
            :this(
                studentCode,
                RlYearModel(yearModel),
                dateOfResponse
            )
    constructor(responseMetadata: ResponseMetadataModel?):
            this(responseMetadata!!.studentCode,
                 responseMetadata.yearModel,
                 responseMetadata.dateOfResponse)
    fun toResponseMetadataModel() = ResponseMetadataModel(studentCode, yearModel!!.toYearModel(), dateOfResponse)

    @LinkingObjects("responseMetadata")
    open val responseModel: RealmResults<RlGradeResponseModel>? = null
}

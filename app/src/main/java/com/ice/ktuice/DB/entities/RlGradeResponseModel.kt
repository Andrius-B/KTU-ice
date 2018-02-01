package com.ice.ktuice.DB.entities

import com.ice.ktuice.scraper.models.GradeResponseModel
import com.ice.ktuice.scraper.models.ResponseMetadataModel
import io.realm.RealmList
import io.realm.RealmObject
import org.jetbrains.annotations.NotNull

/**
 * Created by Andrius on 2/1/2018.
 * TODO merge database models with application level models or centralize the conversions to and fro.
 */
open class RlGradeResponseModel(
        @NotNull
        var responseMetadata:RlResponseMetadata? = null,
        @NotNull
        var responseContent: RealmList<RlGradeModel> = RealmList()
): RealmObject(){
    constructor(metadata: ResponseMetadataModel,
                response: GradeResponseModel):this(RlResponseMetadata(metadata),
                RealmList<RlGradeModel>().apply {
                    response.forEach {
                        add(RlGradeModel(it))
                    }
                })
    fun toGradeResponseModel()
            = GradeResponseModel(200).apply {
        responseContent.forEach {
            add(it.toGradeModel())
        }
    }
}
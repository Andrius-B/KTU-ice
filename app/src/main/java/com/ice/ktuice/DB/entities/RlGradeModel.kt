package com.ice.ktuice.DB.entities

import com.ice.ktuice.scraper.models.GradeModel
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects

open class RlGradeModel(): RealmObject() {
    var name: String = ""
    var id: String = ""
    var semester: String = ""
    var module_code: String = ""
    var module_name: String = ""
    var semester_number: String = ""
    var credits: String = ""
    var language: String = ""
    var professor: String = ""
    var typeId: String = ""
    var type: String? = ""
    var week: String = ""
    /**
     * A list of marks: higher index strings are newer overridden marks.
     */
    var marks: RealmList<String> = RealmList()

    @LinkingObjects("responseContent")
    open val responseModel: RealmResults<RlGradeResponseModel>? = null
    constructor(name: String = "",
                 id: String = "",
                 semester: String = "",
                 module_code: String = "",
                 module_name: String = "",
                 semester_number: String = "",
                 credits: String = "",
                 language: String = "",
                 professor: String = "",
                 typeId: String = "",
                 type: String? = "",
                 week: String = "",
                /**
                 * A list of marks: higher index strings are newer overridden marks.
                 */
                 marks: RealmList<String> = RealmList()):this(){
        this.name = name
        this.id = id
        this.semester = semester
        this.module_code = module_code
        this.module_name = module_name
        this.semester_number = semester_number
        this.credits = credits
        this.language = language
        this.professor = professor
        this.typeId = typeId
        this.type = type
        this.week = week
        this.marks = marks
    }

    constructor(model: GradeModel):this(
            name = model.name,
            id = model.id,
            semester = model.semester,
            module_code = model.module_code,
            module_name = model.module_name,
            semester_number = model.semester_number,
            credits = model.credits,
            language = model.language,
            professor = model.professor,
            typeId = model.typeId,
            type = model.type,
            week = model.week,
            marks = RealmList<String>()){
        marks.addAll(model.marks)
    }

    fun toGradeModel(): GradeModel{
        return GradeModel(name = name,
                id = id,
                semester = semester,
                module_code = module_code,
                module_name = module_name,
                semester_number = semester_number,
                credits = credits,
                language = language,
                professor = professor,
                typeId = typeId,
                type = type,
                week = week,
                marks = marks.toMutableList())
    }
}
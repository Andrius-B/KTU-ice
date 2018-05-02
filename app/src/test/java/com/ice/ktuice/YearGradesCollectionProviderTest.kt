package com.ice.ktuice

import com.ice.ktuice.models.*
import io.realm.RealmList

/**
 * Created by Andrius on 3/14/2018.
 */
class YearGradesCollectionProviderTest {
    companion object {
        /**
         * Creates a year collection model with one year and one semester
         */
        fun createDefaultYearGradesCollection(): YearGradesCollectionModel {
            val moduleList = RealmList<ModuleModel>()

            val semester = "Rudens semestras 2017"
            val semesterNumber = "2017/1" // not sure about what exactly should be here
            //but it should not matter much for these tests
            for(moduleIndex in 0..6){
                val moduleCode = "PB1821035$moduleIndex"
                val moduleName = "Kompiuterinė Grafika$moduleIndex"

                val markList = RealmList<GradeModel>()
                for(markIndex in 0..5){
                    markList.add(
                            GradeModel(
                                    "",
                                    "",
                                    semester,
                                    moduleCode,
                                    moduleName,
                                    semesterNumber,
                                    " ",
                                    " ",
                                    " ",
                                    "KD",
                                    "1",
                                    "$markIndex",
                                    listOf(
                                            "$markIndex"
                                    ))
                    )
                }

                val module = ModuleModel(
                        semester,
                        semesterNumber,
                        moduleCode,
                        moduleName,
                        "", // these are blank here, because irrelevant
                        "",
                        "",
                        "",
                        "",
                        markList
                )
                moduleList.add(module)
            }

            val semesterModel = SemesterModel(
                    semester,
                    semesterNumber,
                    moduleList
            )
            val yearModel = YearGradesModel()
            yearModel.semesterList.add(semesterModel)

            val collectionModel = YearGradesCollectionModel()
            collectionModel.yearList.add(yearModel)
            return collectionModel
        }

        fun addMark(yearGradesCollectionModel: YearGradesCollectionModel): YearGradesCollectionModel {
            val newGrade = GradeModel(
                    "TEST TEST TEST",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "17",
                    listOf("8")
            )
            yearGradesCollectionModel.yearList.last()!!.semesterList.last()!!.moduleList.last()!!.grades.add(newGrade)
            return yearGradesCollectionModel
        }
    }
}
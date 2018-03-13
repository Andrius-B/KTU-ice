package com.ice.ktuice

import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.al.services.yearGradesService.YearGradesServiceImpl
import com.ice.ktuice.models.*
import io.realm.RealmList
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.Year

/**
 * Created by Andrius on 3/13/2018.
 * Testing the year collection comparator
 */
class YearGradesComparatorTest: KoinTest {
    lateinit var defaultYearCollection: YearGradesCollectionModel

    /**
     * Test if the correct comparison is made for the case,
     * when both the web and database versions are the same
     */
    @Test
    fun `No differences`(){
        //preparing the grade collections and mocking the service
        val serviceMock = mock(YearGradesService::class.java)
        `when`(serviceMock.getYearGradesListFromDB()).thenReturn(createDefaultYearGradesCollection())
        `when`(serviceMock.getYearGradesListFromWeb()).thenReturn(createDefaultYearGradesCollection())

        val module: Module = applicationContext {
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { serviceMock as YearGradesService }
        }
        startKoin(listOf(module))
        //a test use case of the comparator
        val service by inject<YearGradesService>()
        val comparator by inject<YearGradesModelComparator>()

        val db = service.getYearGradesListFromDB()!!
        val new = service.getYearGradesListFromWeb()!!

        val differences = comparator.compare(db.yearList.last()!!, new.yearList.last()!!);

        assert(differences.isEmpty())
        closeKoin()
    }

    /**
     * Tests weather the comparator reflects a new added mark
     */
    @Test
    fun `One new mark added`(){
        val defaultGradeCollectionWithAddedGrade = createDefaultYearGradesCollection()
        defaultGradeCollectionWithAddedGrade.yearList.last()!!.semesterList.last()!!.moduleList.last()!!
                .grades.add(
                    GradeModel(
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
                            "",
                            "17",
                            listOf("8")
                    )
                )

        val serviceMock = mock(YearGradesService::class.java)
        `when`(serviceMock.getYearGradesListFromDB()).thenReturn(createDefaultYearGradesCollection())
        `when`(serviceMock.getYearGradesListFromWeb()).thenReturn(defaultGradeCollectionWithAddedGrade)

        val module: Module = applicationContext {
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { serviceMock as YearGradesService }
        }
        startKoin(listOf(module))
        val service by inject<YearGradesService>()
        val comparator by inject<YearGradesModelComparator>()

        val db = service.getYearGradesListFromDB()!!
        val new = service.getYearGradesListFromWeb()!!

        val differences = comparator.compare(db.yearList.last()!!, new.yearList.last()!!);

        assert(differences.size == 1)
        assert(differences.find { it.field == Difference.Field.Grade &&
                                  it.change == Difference.FieldChange.Added &&
                                  it.supplementary!!.javaClass == GradeModel::class.java
                                  } != null)
        closeKoin()
    }

    /**
     * Tests weather the comparator reflects a changed (updated) grade
     */
    @Test
    fun `Mark changed`(){
        val defaultGradeCollectionWithChangedGrade= createDefaultYearGradesCollection()
        defaultGradeCollectionWithChangedGrade.yearList.last()!!.semesterList.last()!!.moduleList.last()!!
                .grades.last().marks.add("10")

        val serviceMock = mock(YearGradesService::class.java)
        `when`(serviceMock.getYearGradesListFromDB()).thenReturn(createDefaultYearGradesCollection())
        `when`(serviceMock.getYearGradesListFromWeb()).thenReturn(defaultGradeCollectionWithChangedGrade)

        val module: Module = applicationContext {
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { serviceMock as YearGradesService }
        }
        startKoin(listOf(module))
        val service by inject<YearGradesService>()
        val comparator by inject<YearGradesModelComparator>()

        val db = service.getYearGradesListFromDB()!!
        val new = service.getYearGradesListFromWeb()!!

        val differences = comparator.compare(db.yearList.last()!!, new.yearList.last()!!);

        assert(differences.size == 1)
        assert(differences.find {
                it.field == Difference.Field.Grade &&
                it.change == Difference.FieldChange.Changed &&
                it.supplementary!!.javaClass == GradeModel::class.java
        } != null)
        closeKoin()
    }


    /**
     * Creates a year collection model with one year and one semester
     */
    private fun createDefaultYearGradesCollection(): YearGradesCollectionModel{
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
}
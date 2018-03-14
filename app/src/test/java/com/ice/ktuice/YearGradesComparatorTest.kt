package com.ice.ktuice

import com.ice.ktuice.TestYearGradesCollectionProvider.Companion.addMark
import com.ice.ktuice.TestYearGradesCollectionProvider.Companion.createDefaultYearGradesCollection
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.Difference
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.*
import io.realm.RealmList
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

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
        closeKoin()

        assertTrue(differences.isEmpty())
    }

    /**
     * Tests weather the comparator reflects a new added mark
     */
    @Test
    fun `One new mark added`(){
        val defaultGradeCollection= createDefaultYearGradesCollection()
        val defaultGradeCollectionWithAddedGrade = addMark(defaultGradeCollection)

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

        val differences = comparator.compare(db.yearList.last()!!, new.yearList.last()!!)

        closeKoin()

        assertTrue(differences.size == 1)
        assertTrue(differences.find { it.field == Difference.Field.Grade &&
                                  it.change == Difference.FieldChange.Added &&
                                  it.supplementary!!.javaClass == GradeModel::class.java
                                  } != null)
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
        closeKoin()

        assertTrue(differences.size == 1)
        assertTrue(differences.find {
                it.field == Difference.Field.Grade &&
                it.change == Difference.FieldChange.Changed &&
                it.supplementary!!.javaClass == GradeModel::class.java
        } != null)
    }
}
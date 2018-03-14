package com.ice.ktuice

import com.ice.ktuice.al.GradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.GradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.al.SyncJobService
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.lang.Thread.sleep

/**
 * Created by Andrius on 3/14/2018.
 */
class SyncJobServiceNotificationTest: KoinTest{

    /**
     * Tests the sync job service with a mocked service, for
     * if there are no notifications thrown when both the
     * web and database grade tables are the same
     */
    @Test
    fun `No notification`(){
        //mocking the year grades service to return the same default result for both of the
        val yearGradesServiceMock = Mockito.mock(YearGradesService::class.java)
        Mockito.`when`(yearGradesServiceMock.getYearGradesListFromDB()).thenReturn(TestYearGradesCollectionProvider.createDefaultYearGradesCollection())
        Mockito.`when`(yearGradesServiceMock.getYearGradesListFromWeb()).thenReturn(TestYearGradesCollectionProvider.createDefaultYearGradesCollection())

        val notificationFactoryMock = Mockito.mock(NotificationFactory::class.java)

        val module: Module = applicationContext {
            provide { yearGradesServiceMock as YearGradesService }
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { notificationFactoryMock as NotificationFactory }
        }
        startKoin(listOf(module))
        val service = SyncJobService()
        service.onStartJob(null)
        sleep(500) // wait for the service to finish
        closeKoin()

        verify(notificationFactoryMock, times(0)).pushNotification(ArgumentMatchers.anyString())
    }

    @Test
    fun `Mark added notification`(){
        val defaultGradeCollection= TestYearGradesCollectionProvider.createDefaultYearGradesCollection()
        val defaultGradeCollectionWithAddedGrade = TestYearGradesCollectionProvider.addMark(defaultGradeCollection)

        val yearGradesServiceMock = mock(YearGradesService::class.java)
        `when`(yearGradesServiceMock.getYearGradesListFromDB()).thenReturn(TestYearGradesCollectionProvider.createDefaultYearGradesCollection())
        `when`(yearGradesServiceMock.getYearGradesListFromWeb()).thenReturn(defaultGradeCollectionWithAddedGrade)

        val notificationFactoryMock = Mockito.mock(NotificationFactory::class.java)

        val module: Module = applicationContext {
            provide { yearGradesServiceMock as YearGradesService }
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { notificationFactoryMock as NotificationFactory }
        }
        startKoin(listOf(module))
        val yearService by inject<YearGradesService>()
        val comparator by inject<YearGradesModelComparator>()

        val db = yearService.getYearGradesListFromDB()!!
        val new = yearService.getYearGradesListFromWeb()!!

        // TODO remove this part, its to simulate what happens in the service
        val differences = comparator.compare(db.yearList.last()!!, new.yearList.last()!!)
        println("Differences in test:${differences.size}")


        val service = SyncJobService()
        service.onStartJob(null)
        sleep(500) // wait for the service to finish
        closeKoin()

        assertTrue(differences.size == 1)
        verify(notificationFactoryMock, times(1)).pushNotification(ArgumentMatchers.anyString())
    }
}
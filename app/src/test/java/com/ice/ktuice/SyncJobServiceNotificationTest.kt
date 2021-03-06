package com.ice.ktuice

import com.ice.ktuice.al.gradeTable.notifications.NotificationFactory
import com.ice.ktuice.al.gradeTable.yearGradesModelComparator.YearGradesModelComparator
import com.ice.ktuice.al.gradeTable.yearGradesModelComparator.YearGradesModelComparatorImpl
import com.ice.ktuice.al.notifications.NotificationSummaryGenerator
import com.ice.ktuice.al.notifications.SyncJob
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.al.settings.AppSettings
import com.ice.ktuice.al.settings.AppSettingsTestImpl
import org.junit.After
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.lang.Exception
import java.lang.Thread.sleep

/**
 * Created by Andrius on 3/14/2018.
 */
class SyncJobServiceNotificationTest: KoinTest{

    @After
    fun cleanup(){
        try {
            closeKoin()
        }catch (e: Exception){
            println(e.message)
        }
    }

    /**
     * Tests the sync job service with a mocked service, for
     * if there are no notifications thrown when both the
     * web and database grade tables are the same
     */
    @Test
    fun `No notification`(){
        //mocking the year grades service to return the same default result for both of the
        val yearGradesServiceMock = mock(YearGradesService::class.java)
        Mockito.`when`(yearGradesServiceMock.getYearGradesListFromDB()).thenReturn(YearGradesCollectionProviderTest.createDefaultYearGradesCollection())
        Mockito.`when`(yearGradesServiceMock.getYearGradesListFromWeb()).thenReturn(YearGradesCollectionProviderTest.createDefaultYearGradesCollection())

        val notificationSummaryGenerator = mock(NotificationSummaryGenerator::class.java)
        `when`(notificationSummaryGenerator.generateSummaryFromDifferences(ArgumentMatchers.anyList())).thenReturn("Test")
        val notificationFactoryMock = mock(NotificationFactory::class.java)

        val module: Module = applicationContext {
            provide { yearGradesServiceMock as YearGradesService }
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { notificationFactoryMock as NotificationFactory }
            provide { notificationSummaryGenerator as NotificationSummaryGenerator }
            provide { AppSettingsTestImpl() as AppSettings }
        }
        startKoin(listOf(module))
        val syncJob = SyncJob()
        syncJob.sync(1)
        sleep(500) // wait for the service to finish
        closeKoin()

        verify(notificationFactoryMock, times(0)).pushNotification(ArgumentMatchers.anyString())
    }

    /**
     * In case a mark is added, we should push a notification
     */
    @Test
    fun `Mark added notification`(){
        val defaultGradeCollection= YearGradesCollectionProviderTest.createDefaultYearGradesCollection()
        val defaultGradeCollectionWithAddedGrade = YearGradesCollectionProviderTest.addMark(defaultGradeCollection)

        val yearGradesServiceMock = mock(YearGradesService::class.java)
        `when`(yearGradesServiceMock.getYearGradesListFromDB()).thenReturn(YearGradesCollectionProviderTest.createDefaultYearGradesCollection())
        `when`(yearGradesServiceMock.getYearGradesListFromWeb()).thenReturn(defaultGradeCollectionWithAddedGrade)

        val notificationSummaryGenerator = mock(NotificationSummaryGenerator::class.java)
        `when`(notificationSummaryGenerator.generateSummaryFromDifferences(ArgumentMatchers.anyList())).thenReturn("Test")

        val notificationFactoryMock = mock(NotificationFactory::class.java)

        val module: Module = applicationContext {
            provide { yearGradesServiceMock as YearGradesService }
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { notificationFactoryMock as NotificationFactory }
            provide { notificationSummaryGenerator as NotificationSummaryGenerator }
            provide { AppSettingsTestImpl() as AppSettings }
        }
        startKoin(listOf(module))

        val syncJob = SyncJob()
        syncJob.sync(1)

        sleep(1000) // wait for the service to finish
        //(this is because, the comparison and fetching of the grade tables happens in a background thread)
        closeKoin()

        verify(notificationFactoryMock, times(1)).pushNotification(ArgumentMatchers.anyString())
    }

    @Test
    fun `Mark changed notification`(){
        val defaultGradeCollectionWithChangedGrade= YearGradesCollectionProviderTest.createDefaultYearGradesCollection()
        defaultGradeCollectionWithChangedGrade.yearList.last()!!.semesterList.last()!!.moduleList.last()!!
                .grades.last()!!.marks.add("10")


        val yearGradesServiceMock = mock(YearGradesService::class.java)
        `when`(yearGradesServiceMock.getYearGradesListFromDB()).thenReturn(YearGradesCollectionProviderTest.createDefaultYearGradesCollection())
        `when`(yearGradesServiceMock.getYearGradesListFromWeb()).thenReturn(defaultGradeCollectionWithChangedGrade)

        val notificationSummaryGenerator = mock(NotificationSummaryGenerator::class.java)
        `when`(notificationSummaryGenerator.generateSummaryFromDifferences(ArgumentMatchers.anyList())).thenReturn("Test")

        val notificationFactoryMock = mock(NotificationFactory::class.java)

        val module: Module = applicationContext {
            provide { yearGradesServiceMock as YearGradesService }
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { notificationFactoryMock as NotificationFactory }
            provide { notificationSummaryGenerator as NotificationSummaryGenerator }
            provide { AppSettingsTestImpl() as AppSettings }
        }

        startKoin(listOf(module))

        val syncJob = SyncJob()
        syncJob.sync(1)

        sleep(1000) // wait for the service to finish
        //(this is because, the comparison and fetching of the grade tables happens in a background thread)
        closeKoin()

        verify(notificationFactoryMock, times(1)).pushNotification(ArgumentMatchers.anyString())
    }

    @Test
    fun `Only one notification`(){
        /**
         * Even with multiple changes to the grade table, there should only be a single notification
         */
        var defaultGradeCollectionWithChangedGrade= YearGradesCollectionProviderTest.createDefaultYearGradesCollection()
        defaultGradeCollectionWithChangedGrade.yearList.last()!!.semesterList.last()!!.moduleList.last()!!
                .grades.last()!!.marks.add("10")
        defaultGradeCollectionWithChangedGrade = YearGradesCollectionProviderTest.addMark(defaultGradeCollectionWithChangedGrade)
        defaultGradeCollectionWithChangedGrade = YearGradesCollectionProviderTest.addMark(defaultGradeCollectionWithChangedGrade)


        val yearGradesServiceMock = mock(YearGradesService::class.java)
        `when`(yearGradesServiceMock.getYearGradesListFromDB()).thenReturn(YearGradesCollectionProviderTest.createDefaultYearGradesCollection())
        `when`(yearGradesServiceMock.getYearGradesListFromWeb()).thenReturn(defaultGradeCollectionWithChangedGrade)

        val notificationSummaryGenerator = mock(NotificationSummaryGenerator::class.java)
        `when`(notificationSummaryGenerator.generateSummaryFromDifferences(ArgumentMatchers.anyList())).thenReturn("Test")

        val notificationFactoryMock = mock(NotificationFactory::class.java)

        val module: Module = applicationContext {
            provide { yearGradesServiceMock as YearGradesService }
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { notificationFactoryMock as NotificationFactory }
            provide { notificationSummaryGenerator as NotificationSummaryGenerator }
            provide { AppSettingsTestImpl() as AppSettings }
        }

        startKoin(listOf(module))

        val syncJob = SyncJob()
        syncJob.sync(1)

        sleep(1000) // wait for the service to finish
        //(this is because, the comparison and fetching of the grade tables happens in a background thread)
        closeKoin()

        verify(notificationFactoryMock, times(1)).pushNotification(ArgumentMatchers.anyString())
    }

    @Test
    fun `Notifications disabled`(){
        var defaultGradeCollectionWithChangedGrade= YearGradesCollectionProviderTest.createDefaultYearGradesCollection()
        defaultGradeCollectionWithChangedGrade.yearList.last()!!.semesterList.last()!!.moduleList.last()!!
                .grades.last()!!.marks.add("10")
        defaultGradeCollectionWithChangedGrade = YearGradesCollectionProviderTest.addMark(defaultGradeCollectionWithChangedGrade)
        defaultGradeCollectionWithChangedGrade = YearGradesCollectionProviderTest.addMark(defaultGradeCollectionWithChangedGrade)


        val yearGradesServiceMock = mock(YearGradesService::class.java)
        `when`(yearGradesServiceMock.getYearGradesListFromDB()).thenReturn(YearGradesCollectionProviderTest.createDefaultYearGradesCollection())
        `when`(yearGradesServiceMock.getYearGradesListFromWeb()).thenReturn(defaultGradeCollectionWithChangedGrade)

        val notificationSummaryGenerator = mock(NotificationSummaryGenerator::class.java)
        `when`(notificationSummaryGenerator.generateSummaryFromDifferences(ArgumentMatchers.anyList())).thenReturn("Test")

        val notificationFactoryMock = mock(NotificationFactory::class.java)

        val module: Module = applicationContext {
            provide { yearGradesServiceMock as YearGradesService }
            provide { YearGradesModelComparatorImpl() as YearGradesModelComparator }
            provide { notificationFactoryMock as NotificationFactory }
            provide { notificationSummaryGenerator as NotificationSummaryGenerator }
            provide { AppSettingsTestImpl() as AppSettings }
        }

        startKoin(listOf(module))

        val syncJob = SyncJob()
        syncJob.sync(0)

        sleep(1000) // wait for the service to finish
        //(this is because, the comparison and fetching of the grade tables happens in a background thread)
        closeKoin()

        verify(notificationFactoryMock, times(0)).pushNotification(ArgumentMatchers.anyString())
    }
}
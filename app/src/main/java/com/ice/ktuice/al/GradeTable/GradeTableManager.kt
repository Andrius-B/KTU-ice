package com.ice.ktuice.al.GradeTable

import com.ice.ktuice.al.GradeTable.gradeTableModels.GradeTableFactory
import com.ice.ktuice.al.GradeTable.gradeTableModels.GradeTableModel
import com.ice.ktuice.al.GradeTable.gradeTableModels.SemesterAdapterItem
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.YearGradesRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.al.services.userService.UserService
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearGradesModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.scraperService.exceptions.AuthenticationException
import com.ice.ktuice.scraperService.ScraperService
import io.realm.RealmList
import io.realm.RealmResults
import org.jetbrains.anko.getStackTraceString
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/15/2018.
 * A helper class to contain the logic of the grade table and supply the models
 */
class GradeTableManager: KoinComponent {
    private val userService: UserService by inject()
    private val yearGradesService: YearGradesService by inject()

    fun constructGradeTableModel(yearGradesList: YearGradesCollectionModel): GradeTableModel?{
        try{
            val table = GradeTableFactory.buildGradeTableFromYearGradesModel(yearGradesList)
            println("Printing the grade table!")
            println("Table:" + table.toString())
            println("Seen weeks:" + table.getWeekListString())
            table.printRowCounts()
            return table
        }catch (it: Exception){
            when(it.javaClass){
                AuthenticationException::class.java -> {
                    try {
                        println("refreshing login cookies!")
                        val newLoginModel = userService.refreshLoginCookies()!!
                        println("login cookies refreshed, should initialize grade table")
                        return constructGradeTableModel(yearGradesList)
                    }catch (e: Exception){
                        println(e.getStackTraceString())
                    }
                }
            }
            println(it.getStackTraceString())
        }
        return null
    }


    fun constructSemesterAdapterSpinnerItemList(yearsList: YearGradesCollectionModel):List<SemesterAdapterItem>{
        val itemList = mutableListOf<SemesterAdapterItem>()
        yearsList.forEach {
            val year = it.year
            it.semesterList.forEach {
                itemList.add(SemesterAdapterItem(it.semester, it.semester_number, YearModel(year.id, year.year)))
            }
        }
        return itemList
    }

}
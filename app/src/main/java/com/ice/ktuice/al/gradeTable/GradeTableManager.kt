package com.ice.ktuice.al.gradeTable

import android.util.Log
import com.ice.ktuice.al.gradeTable.gradeTableModels.GradeTableFactory
import com.ice.ktuice.al.gradeTable.gradeTableModels.GradeTableModel
import com.ice.ktuice.al.gradeTable.gradeTableModels.SemesterAdapterItem
import com.ice.ktuice.al.logger.IceLog
import com.ice.ktuice.al.logger.info
import com.ice.ktuice.models.YearGradesCollectionModel
import com.ice.ktuice.models.YearModel
import com.ice.ktuice.al.services.scrapers.base.exceptions.AuthenticationException
import org.koin.standalone.KoinComponent

/**
 * Created by Andrius on 2/15/2018.
 * A helper class to contain the logic of the grade table and supply the models
 */
class GradeTableManager: KoinComponent, IceLog {
    fun constructGradeTableModel(yearGradesList: YearGradesCollectionModel): GradeTableModel?{
        try{
            val table = GradeTableFactory.buildGradeTableFromYearGradesModel(yearGradesList)
            table.printRowCounts()
            return table
        }catch (it: Exception){
            when(it.javaClass){
                AuthenticationException::class.java -> {
                    try {
                        //recursive auth trying
                        return constructGradeTableModel(yearGradesList)
                    }catch (e: Exception){
                        info(Log.getStackTraceString(e))
                    }
                }
            }
            info(Log.getStackTraceString(it))
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
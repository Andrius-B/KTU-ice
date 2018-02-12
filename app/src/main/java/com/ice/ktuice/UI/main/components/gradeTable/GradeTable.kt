package com.ice.ktuice.UI.main.components.gradeTable

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.*
import com.ice.ktuice.AL.GradeTableModels.GradeTableFactory
import com.ice.ktuice.AL.GradeTableModels.GradeTableModel
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.GradeResponseRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.UI.main.GradeTableCellDetailsDialog
import com.ice.ktuice.scraper.models.GradeResponseModel
import com.ice.ktuice.scraper.models.LoginModel
import com.ice.ktuice.scraper.models.ResponseMetadataModel
import com.ice.ktuice.scraper.scraperService.Exceptions.AuthenticationException
import com.ice.ktuice.scraper.scraperService.ScraperService
import io.realm.Realm
import kotlinx.android.synthetic.main.grade_table_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.uiThread
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

/**
 * Created by Andrius on 1/29/2018.
 */
class GradeTable(c: Context, attrs: AttributeSet?): LinearLayout(c, attrs), KoinComponent {
    private val tableCellDetailsDialog: GradeTableCellDetailsDialog = GradeTableCellDetailsDialog(context)

    private val preferenceRepository: PreferenceRepository by inject()
    private val gradeRepository: GradeResponseRepository by inject()
    private val loginRepository: LoginRepository by inject()

    private val CELL_PADDING_H: Int = context.resources.getInteger(R.integer.grade_table_cell_padding_horizontal)
    private val CELL_PADDING_V: Int = context.resources.getInteger(R.integer.grade_table_cell_padding_vertical)
    private val CELL_TEXT_SIZE = context.resources.getInteger(R.integer.grade_table_cell_text_size).toFloat()



    constructor(c: Context): this(c, null)

    init {

        inflate(context, R.layout.grade_table_layout, this)

        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if(requestedStudentId.isBlank()){
            println("StudentCode not found, quitting!")
            throw NullPointerException("Student code is not found, can not initialize the grade table component!")
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId, Realm.getDefaultInstance())
        if(loginModel == null){
            println("Login model is null!")
            throw NullPointerException("Login model for the requested code is null, can not initialize the grade table component")
        }
        try{
            val gradeResponseRepositoryContent = gradeRepository.getByYearModel(loginModel.studentId, loginModel.studentSemesters[0], Realm.getDefaultInstance())
            println("Grade table null:"+(gradeResponseRepositoryContent == null))

            initializeGradeTable(loginModel, gradeResponseRepositoryContent)
        }catch (e:Exception){
            println(e.getStackTraceString())
        }
    }

    private fun createViewForModel(gradeTableModel: GradeTableModel){
        val rowModelList = gradeTableModel.getRows()
        val weekModelList = gradeTableModel.getTotalWeekList()

        //header generation
        val tableRow = TableRow(context)
        weekModelList?.forEach {
            val weekText = TextView(context)
            weekText.text = it.week
            tableRow.addView(weekText)
        }
        grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        // add a spacer dummy text to keep the spacing even for the table and module names
        grade_table_headers.addView(TextView(context), ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        rowModelList?.forEach {
            val tableRow = TableRow(context)
            val moduleName = it.moduleModel.module_name
            val moduleNameText = TextView(context)
            moduleNameText.text = moduleName
            moduleNameText.setSingleLine(true)
            moduleNameText.ellipsize = TextUtils.TruncateAt.END
            moduleNameText.maxLines = 1
            moduleNameText.setPadding(CELL_PADDING_H, CELL_PADDING_V, CELL_PADDING_V, CELL_PADDING_H)
            moduleNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, CELL_TEXT_SIZE)
            moduleNameText.setBackgroundResource(R.drawable.grade_cell_background)

            println(String.format("Adding row:%s", it.moduleModel.module_name))
            grade_table_headers.addView(moduleNameText)

            weekModelList?.forEach { weekModel ->
                val markCellText = TextView(context)
                markCellText.setPadding(CELL_PADDING_H, CELL_PADDING_V, CELL_PADDING_V, CELL_PADDING_H)
                markCellText.setTextSize(TypedValue.COMPLEX_UNIT_SP, CELL_TEXT_SIZE)
                val cellModel = it.getByWeekModel(weekModel)
                markCellText.text = cellModel?.getDisplayString() // default is empty cell
                markCellText.setOnClickListener{
                    markCellText.post({
                        tableCellDetailsDialog.CellModel = cellModel
                        tableCellDetailsDialog.show()
                    })
                }
                markCellText.setBackgroundResource(R.drawable.grade_cell_background)
                tableRow.addView(markCellText)
            }
            grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        }
    }

    private fun initializeGradeTable(login: LoginModel, dbResp: GradeResponseModel? = null){
        doAsync(
                {
                    when(it.javaClass){
                        AuthenticationException::class.java -> {
                            try {
                                println("refreshing login cookies!")
                                val newLoginModel = refreshLoginCookies(login)
                                println("login cookies refreshed, initializing grade table")
                                initializeGradeTable(newLoginModel)
                                println("grade table initialized!")
                            }catch (e: Exception){
                                println(e.getStackTraceString())
                            }
                        }
                    }
                    println(it.getStackTraceString())
                },
                {
                    println("Db table found:"+(dbResp != null))
                    println(String.format("Getting grades for semester:%s, id: %s", login.studentSemesters[0].year, login.studentSemesters[0].id))

                    val loginModel = refreshLoginCookies(login)
                    val marks = ScraperService.getGrades(loginModel, loginModel.studentSemesters[0])
                    val table = GradeTableFactory.buildGradeTableFromYearGradesModel(marks)
                    table.selectSemester(1)
                    println("Printing the grade table!")
                    println("Table:" + table.toString())
                    println("Seen weeks:" + table.getWeekListString())
                    table.printRowCounts()
                    uiThread ({
                        //gradeRepository.createOrUpdate(marks, ResponseMetadataModel(loginModel.studentId, loginModel.studentSemesters[0], Date()), Realm.getDefaultInstance())
                        println("Creating view!")
                        createViewForModel(table)
                    })
                })
    }

    private fun refreshLoginCookies(loginModel: LoginModel): LoginModel {
        println(String.format("login cookies username:%s ,pw:%s",loginModel.username, loginModel.password))
        val newLoginModelResponse = ScraperService.login(loginModel.username, loginModel.password)
        println("refreshing login cookies response:"+newLoginModelResponse.statusCode)
        val newLoginModel = newLoginModelResponse.loginModel!!
        loginRepository.createOrUpdate(newLoginModel, Realm.getDefaultInstance())
        return newLoginModel
    }

}
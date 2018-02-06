package com.ice.ktuice.UI.main.components.gradeTable

import android.content.ComponentCallbacks
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import com.ice.ktuice.AL.GradeTableRowModel.GradeTableFactory
import com.ice.ktuice.AL.GradeTableRowModel.GradeTableModel
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.GradeResponseRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
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
import org.koin.android.ext.android.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

/**
 * Created by Andrius on 1/29/2018.
 */
class GradeTable(c: Context, attrs: AttributeSet?): LinearLayout(c, attrs), KoinComponent {

    private val preferenceRepository: PreferenceRepository by inject()
    private val gradeRepository: GradeResponseRepository by inject()
    private val loginRepository: LoginRepository by inject()

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

        val gradeResponseRepositoryContent = gradeRepository.getByYearModel(loginModel.studentId, loginModel.studentSemesters[0], Realm.getDefaultInstance())
        println("Grade table null:"+(gradeResponseRepositoryContent == null))

        initializeGradeTable(loginModel, gradeResponseRepositoryContent)
    }

    fun createViewForModel(gradeTableModel: GradeTableModel){
        val rowModelList = gradeTableModel.getRows()
        val weekModelList = gradeTableModel.getTotalWeekList()

        //header generation
        val tableRow = TableRow(context)
        val header = TextView(context)
        //header.text  = context.getText(R.string.grade_table_week_header)
        //tableRow.addView(header)
        weekModelList.forEach {
            val weekText = TextView(context)
            weekText.text = it.week
            tableRow.addView(weekText)
        }
        grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))


        grade_table_headers.addView(TextView(context)) // add a spacer dummy text
        rowModelList.forEach{
            val tableRow = TableRow(context)
            val moduleName = it.moduleModel.module_name
            val moduleNameText = TextView(context)
            moduleNameText.text = moduleName
            moduleNameText.setSingleLine(true)
            moduleNameText.ellipsize = TextUtils.TruncateAt.END
            moduleNameText.maxLines = 1

            grade_table_headers.addView(moduleNameText)

            weekModelList.forEach{weekModel ->

                val markCellText = TextView(context)
                markCellText.setPadding(6,0,6,0)
                val cellModel = it.getByWeekModel(weekModel)
                markCellText.text = cellModel?.getDisplayString() ?: "" // default is empty cell
                tableRow.addView(markCellText)
            }
            grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

            grade_table_table_layout.requestLayout()
            grade_table_grade_scroll_view.requestLayout()
            grade_table_grade_scroll_view_content.requestLayout()
            requestLayout()
        }
    }

    private fun initializeGradeTable(loginModel: LoginModel, dbResp: GradeResponseModel? = null){
        doAsync(
                {
                    when(it.javaClass){
                        AuthenticationException::class.java -> {
                            try {
                                println("refreshing login cookies!")
                                val newLoginModel = refreshLoginCookies(loginModel)
                                println("login cookies refreshed, initializing grade table")
                                initializeGradeTable(newLoginModel)
                                println("grade table initialized!")
                            }catch (e: Exception){
                                println(e.getStackTraceString())
                            }
                        }
                    }
                },
                {
                    println("Db table found:"+(dbResp != null))
                    println(String.format("Getting grades for semester:%s, id: %s", loginModel.studentSemesters[0].year, loginModel.studentSemesters[0].id))

                    val marks = dbResp ?: ScraperService.getGrades(loginModel, loginModel.studentSemesters[0])
                    Log.d("INFO", String.format("GradeResponseModel code:"+marks.statusCode))

                    val table = GradeTableFactory.buildGradeTableFromMarkResponse(marks)
                    println("Printing the grade table!")
                    println("Table:" + table.toString())
                    println("Seen weeks:" + table.getWeekListString())
                    table.printRowCounts()
                    uiThread ({
                        gradeRepository.createOrUpdate(marks, ResponseMetadataModel(loginModel.studentId, loginModel.studentSemesters[0], Date()), Realm.getDefaultInstance())
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
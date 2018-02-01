package com.ice.ktuice.UI.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.Gson
import com.ice.ktuice.AL.GradeTableRowModel.GradeTableFactory
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.GradeResponseRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.scraper.models.*
import com.ice.ktuice.scraper.scraperService.Exceptions.AuthenticationException
import com.ice.ktuice.scraper.scraperService.ScraperService
import com.ice.ktuice.scraper.scraperService.handlers.DataHandler
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val loginRepository:LoginRepository by inject()
    private val preferenceRepository: PreferenceRepository by inject()
    private val gradeRepository: GradeResponseRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if(requestedStudentId.isBlank()){
            println("StudentCode not found, quitting!")
            return@onCreate
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId, Realm.getDefaultInstance())
        if(loginModel == null){
            println("Login model is null!")
            return@onCreate
        }
        println("login model created!")
        info_semesters_found.text = loginModel.studentSemesters.size.toString()
        info_student_code.text = loginModel.studentId
        info_student_name.text = loginModel.studentName
        logout_btn.setOnClickListener{
            runOnUiThread{
                this.finish()
                preferenceRepository.setValue(R.string.shared_preference_file_key, "") // clear out the logged in user code from prefrences
            }
        }

        val gradeResponseRepositoryContent = gradeRepository.getByYearModel(loginModel.studentId, loginModel.studentSemesters[0], Realm.getDefaultInstance())
        println("Grade table null:"+(gradeResponseRepositoryContent == null))

        initializeGradeTable(loginModel, gradeResponseRepositoryContent)
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
                        if(grade_table_main.isAttachedToWindow)
                            grade_table_main.createViewForModel(table)
                    })
                })
    }

    private fun refreshLoginCookies(loginModel: LoginModel): LoginModel{
        println(String.format("login cookies username:%s ,pw:%s",loginModel.username, loginModel.password))
        val newLoginModelResponse = ScraperService.login(loginModel.username, loginModel.password)
        println("refreshing login cookies response:"+newLoginModelResponse.statusCode)
        val newLoginModel = newLoginModelResponse.loginModel!!
        loginRepository.createOrUpdate(newLoginModel, Realm.getDefaultInstance())
        return newLoginModel
    }

}

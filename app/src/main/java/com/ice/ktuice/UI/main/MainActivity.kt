package com.ice.ktuice.UI.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.ice.ktuice.AL.GradeTableRowModel.GradeTableFactory
import com.ice.ktuice.DB.entities.RlUserModel
import com.ice.ktuice.R
import com.ice.ktuice.scraper.models.LoginModel
import com.ice.ktuice.scraper.scraperService.handlers.DataHandler
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val requestedStudentId = intent.getStringExtra("vidko")
        if(requestedStudentId == null){
            println("StudentCode not found, quitting!")
            return@onCreate
        }

        val realm = Realm.getDefaultInstance()
        println("Reading realm objects")
        val rlu = realm
                .where(RlUserModel::class.java)
                .equalTo("studId", requestedStudentId)
                .findFirst()
        println("RLU read:"+(rlu != null))
        val loginModel = RlUserModel.toLoginModel(rlu!!)!! // if something is null here, just quit now
        println("login model created!")
        info_semesters_found.text = loginModel.studentSemesters.size.toString()
        info_student_code.text = loginModel.studentId
        info_student_name.text = loginModel.studentName

        doAsync {
            println("Getting grades!")
            val api = DataHandler()
            val gson = Gson()
            val marks = api.getGrades(loginModel, loginModel.studentSemesters[0])
            val table = GradeTableFactory.buildGradeTableFromMarkResponse(marks)
            println(table.toString())
        }
    }

}

package com.ice.ktuice.UI.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ice.ktuice.DB.entities.RlUserModel
import com.ice.ktuice.R
import com.ice.ktuice.scraper.models.LoginModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val realm = Realm.getDefaultInstance()
        println("Reading realm objects")

        val rlu = realm
                .where(RlUserModel::class.java)
                .findFirst()
        println("RLU read:"+(rlu != null))
        val loginModel = RlUserModel.toLoginModel(rlu!!)!! // if something is null here, just quit now
        println("login model created!")
        info_semesters_found.text = loginModel.studentSemesters.size.toString()
        info_student_code.text = loginModel.studentId
        info_student_name.text = loginModel.studentName
    }

}

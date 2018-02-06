package com.ice.ktuice.UI.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ice.ktuice.DAL.repositories.gradeResponseRepository.GradeResponseRepository
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val loginRepository:LoginRepository by inject()
    private val preferenceRepository: PreferenceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if(requestedStudentId.isBlank()){
            println("StudentCode not found, quitting!")
            logout()
            return@onCreate
        }else{
            println("Student code is:"+requestedStudentId)
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId, Realm.getDefaultInstance())
        if(loginModel == null){
            println("Login model is null!")
            logout()
            return@onCreate
        }
        println("login model created!")
        info_semesters_found.text = loginModel.studentSemesters.size.toString()
        info_student_code.text = loginModel.studentId
        info_student_name.text = loginModel.studentName
        logout_btn.setOnClickListener{
            runOnUiThread{
                logout()
            }
        }
    }

    private fun logout(){
        this.finish()
        preferenceRepository.setValue(R.string.shared_preference_file_key, "") // clear out the logged in user code from prefrences
    }

}

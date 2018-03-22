package com.ice.ktuice.ui.main.fragments

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.al.notifications.SyncJobService
import com.ice.ktuice.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_grades.*
import org.jetbrains.anko.doAsync
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent

/**
 * Created by Andrius on 2/24/2018.
 */
class FragmentGrades: Fragment(), KoinComponent {

    private val loginRepository: LoginRepository by inject()
    private val preferenceRepository: PreferenceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Creating Grades fragment!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_grades, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doAsync {
            scheduleJob()
            val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
            if(requestedStudentId.isBlank()){
                println("StudentCode not found, quitting!")
                logout()
            }else{
                println("Student code is:$requestedStudentId")
            }
            val loginModel = loginRepository.getByStudCode(requestedStudentId)
            if(loginModel == null){
                println("Login model is null!")
                logout()
            }
            println("login model created!")
            loginModel!!
            info_semesters_found.text = loginModel.studentSemesters.size.toString()
            info_student_code.text = loginModel.studentId
            info_student_name.text = loginModel.studentName

            logout_btn.setOnClickListener{
                activity?.runOnUiThread {
                    logout()
                }
            }

            test_button.setOnClickListener{
                println("job scheduling test button tap!")
                scheduleJob()
            }
        }
    }

    private fun logout(){
        this.activity?.runOnUiThread{
                preferenceRepository.setValue(R.string.logged_in_user_code, "") // clear out the logged in user code from prefrences
                val intent = Intent(this.activity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.activity?.finish()
            }
    }

    private fun scheduleJob(){
        val serviceComponent = ComponentName(this.activity, SyncJobService::class.java)
        val builder = JobInfo.Builder(0, serviceComponent)
        builder.setMinimumLatency(1000*5)
        val jobScheduler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.activity?.applicationContext?.getSystemService(JobScheduler::class.java)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        jobScheduler?.schedule(builder.build())
    }

}
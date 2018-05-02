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
import android.os.PersistableBundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * Created by Andrius on 2/24/2018.
 * The main fragment of the application:
 * displays a Grade Table component and lets the student log out
 */
class FragmentGrades: Fragment(), KoinComponent, AnkoLogger {

    private val loginRepository: LoginRepository by inject()
    private val preferenceRepository: PreferenceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info("Creating Grades fragment!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_grades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doAsync {
            val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
            if(requestedStudentId.isBlank()){
                //info("StudentCode not found, quitting!")
                logout()
            }else{
                //info("Student code is:$requestedStudentId")
            }
            val loginModel = loginRepository.getByStudCode(requestedStudentId)
            if(loginModel == null){
                info("Login model is null!")
                logout()
            }
            //info("login model created!")
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
                //info("job scheduling test button tap!")
                scheduleJob(true)
                //scheduleJob(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        scheduleJob(false)
        // shedule the notifications to be polled for  whenever the application is paused / stopped
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

    private fun scheduleJob(instant: Boolean){
        val serviceComponent = ComponentName(this.activity, SyncJobService::class.java)
        val bundle = PersistableBundle()
        val notificationFlag = if(instant) 0 else 1
        bundle.putInt(context!!.resources.getString(R.string.notification_enabled_flag), notificationFlag)
        val builder = JobInfo.Builder(0, serviceComponent)
                             .setExtras(bundle)
        if(!instant)
            builder.setPeriodic(1000*60*180)
            //TODO move period time to configuration, not inline
        else
            builder.setOverrideDeadline(100)


        val jobScheduler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.activity?.applicationContext?.getSystemService(JobScheduler::class.java)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        jobScheduler?.cancel(0)
        jobScheduler?.schedule(builder.build())
    }

}
package com.ice.ktuice.ui.main.fragments

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.arch.lifecycle.Observer
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
import android.support.constraint.ConstraintLayout
import com.ice.ktuice.al.services.yearGradesService.YearGradesService
import com.ice.ktuice.ui.main.components.GradeTable
import com.ice.ktuice.viewModels.gradesFragment.GradesFragmentViewModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.runOnUiThread


/**
 * Created by Andrius on 2/24/2018.
 * The main fragment of the application:
 * displays a Grade Table component and lets the student log out
 */
class FragmentGrades: Fragment(), KoinComponent, AnkoLogger {
    private val yearGradesService: YearGradesService by inject()
    private val viewModel: GradesFragmentViewModel by inject()
    private lateinit var gradeTableView: GradeTable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info("Creating Grades fragment!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_grades, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gradeTableView = GradeTable(this.activity!!)
        grade_table_view_container.addView(gradeTableView)

        viewModel.grades.observe(this, Observer {
            if(it != null) {
                this.activity?.runOnUiThread {
                    // TODO reduce the view-model to only contain a key, or add PDO's
                    // realm can not pass objects between threads..
                    // so we re-fetch the grades here
                    val grades = yearGradesService.getYearGradesListFromDB()
                    if(grades != null) {
                        gradeTableView.updateGradeTable(grades, viewModel.selectedSemesterNumber.value, viewModel.selectedYear.value)
                    }
                }
            }
        })

        viewModel.loginModel.observe(this, Observer {  loginModel->
            this.activity?.runOnUiThread {
                loginModel!!
                info_semesters_found.text = loginModel.studentSemesters.size.toString()
                info_student_code.text = loginModel.studentId
                info_student_name.text = loginModel.studentName
            }
        })

        doAsync {

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
        viewModel.logoutCurrentUser(this.activity)
    }

    private fun scheduleJob(instant: Boolean){
        val serviceComponent = ComponentName(this.activity!!, SyncJobService::class.java)
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
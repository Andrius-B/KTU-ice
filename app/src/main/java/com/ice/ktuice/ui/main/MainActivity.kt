package com.ice.ktuice.ui.main

import android.content.ComponentName
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ice.ktuice.al.SyncJobService
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.support.graphics.drawable.Animatable2Compat
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private val loginRepository:LoginRepository by inject()
    private val preferenceRepository: PreferenceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scheduleJob()
        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if(requestedStudentId.isBlank()){
            println("StudentCode not found, quitting!")
            logout()
            return@onCreate
        }else{
            println("Student code is:"+requestedStudentId)
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId)
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

        time_table_button.setOnClickListener {
            val intent = Intent(this, TimeTableActivity::class.java)
            startActivity(intent)
        }

        test_button.setOnClickListener{
            println("job scheduling test button tap!")
            scheduleJob()
        }

        prepareDrawerButtons()
    }

    private fun logout(){
        this.finish()
        preferenceRepository.setValue(R.string.shared_preference_file_key, "") // clear out the logged in user code from prefrences
    }

    private fun scheduleJob(){
        val serviceComponent = ComponentName(this, SyncJobService::class.java)
        val builder = JobInfo.Builder(0, serviceComponent)
        builder.setMinimumLatency(1000*5)
        val jobScheduler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            applicationContext.getSystemService(JobScheduler::class.java)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        jobScheduler.schedule(builder.build())
    }

    // TODO move to another file
    private fun prepareDrawerButtons(){
        val buttonList = mutableListOf<Pair<TextView, Int>>()
        buttonList.add(Pair(marks_button, R.drawable.avd_table_to_square))
        buttonList.add(Pair(student_info_button, R.drawable.avd_account_to_square))
        buttonList.forEach{
        //if(it is TextView) {
            println("Registered click listener for textview with text:"+it.first.text)
            val drawable = AnimatedVectorDrawableCompat.create(applicationContext, it.second)!!
            it.first.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            it.first.invalidate()
            println("drawables set!")
            drawable.registerAnimationCallback(DrawableAnimationCallback(applicationContext, it.first, it.second))
            it.first.setOnClickListener {
                println("item click!")
                drawable.start()
            }
        //}
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private class DrawableAnimationCallback(val context: Context, val textView: TextView, val resource: Int): Animatable2Compat.AnimationCallback() {

        override fun onAnimationEnd(drawable: Drawable?) {
            //super.onAnimationEnd(drawable)
            val newDrawable = AnimatedVectorDrawableCompat.create(context, resource)!!
            newDrawable.registerAnimationCallback(this)
            textView.setCompoundDrawablesWithIntrinsicBounds(null, newDrawable, null, null)
            textView.setOnClickListener {
                println("item click!")
                newDrawable.start()
            }
        }
    }
}

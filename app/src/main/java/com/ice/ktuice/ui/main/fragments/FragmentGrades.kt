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
import android.widget.LinearLayout
import com.ice.ktuice.DAL.repositories.loginRepository.LoginRepository
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.al.SyncJobService
import kotlinx.android.synthetic.main.fragment_grades.*
import org.jetbrains.anko.runOnUiThread
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
        scheduleJob()
        val requestedStudentId = preferenceRepository.getValue(R.string.logged_in_user_code)
        if(requestedStudentId.isBlank()){
            println("StudentCode not found, quitting!")
            logout()
            return@onViewCreated
        }else{
            println("Student code is:"+requestedStudentId)
        }
        val loginModel = loginRepository.getByStudCode(requestedStudentId)
        if(loginModel == null){
            println("Login model is null!")
            logout()
            return@onViewCreated
        }
        println("login model created!")
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

    private fun logout(){
        this.activity?.finish()
        preferenceRepository.setValue(R.string.shared_preference_file_key, "") // clear out the logged in user code from prefrences
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


// TODO implement icons AVD in the tab layouts (this code left here for inspiration)
//    private fun prepareDrawerButtons(){
//        val buttonList = mutableListOf<Pair<TextView, Int>>()
//        buttonList.add(Pair(marks_button, R.drawable.avd_table_to_square))
//        buttonList.add(Pair(student_info_button, R.drawable.avd_account_to_square))
//        buttonList.forEach{
//            println("Registered click listener for textview with text:"+it.first.text)
//            val drawable = AnimatedVectorDrawableCompat.create(applicationContext, it.second)!!
//            it.first.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
//            it.first.invalidate()
//            println("drawables set!")
//            drawable.registerAnimationCallback(DrawableAnimationCallback(applicationContext, it.first, it.second))
//            it.first.setOnClickListener {
//                println("item click!")
//                drawable.start()
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private class DrawableAnimationCallback(val context: Context, val textView: TextView, val resource: Int): Animatable2Compat.AnimationCallback() {
//
//        override fun onAnimationEnd(drawable: Drawable?) {
//            //super.onAnimationEnd(drawable)
//            val newDrawable = AnimatedVectorDrawableCompat.create(context, resource)!!
//            newDrawable.registerAnimationCallback(this)
//            textView.setCompoundDrawablesWithIntrinsicBounds(null, newDrawable, null, null)
//            textView.setOnClickListener {
//                println("item click!")
//                newDrawable.start()
//            }
//        }
//    }

}
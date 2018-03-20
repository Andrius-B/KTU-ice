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
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.ActionBar
import android.widget.TextView
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekViewEvent
import com.ice.ktuice.ui.adapters.FragmentAdapter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_activity_view_pager.adapter = FragmentAdapter(this.supportFragmentManager)
    }
}

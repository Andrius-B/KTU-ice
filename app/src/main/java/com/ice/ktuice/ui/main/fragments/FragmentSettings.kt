package com.ice.ktuice.ui.main.fragments

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.ice.ktuice.R
import com.ice.ktuice.al.settings.AppSettings
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class FragmentSettings: Fragment(), KoinComponent, AnkoLogger{

    private val settings: AppSettings by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info("Creating Settings fragment!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.current_theme_spinner.adapter =  ArrayAdapter<String>(this.context!!,
                                                android.R.layout.simple_spinner_dropdown_item,
                                                resources.getStringArray(R.array.themes))
        this.current_theme_spinner.setSelection(settings.currentThemePos)
        setSpinnerListener()

        this.notifications_new_grades_switch.isChecked = settings.gradeNotificationsEnabled
        setGradeUpdateSwitchListener()
        this.current_lecture_notification_switch.isChecked = settings.persistentLectureNotificationEnabled
        setLectureNotificationSwitchListener()

    }

    /**
     * Sync the settings to the settings implementation
     */
    private fun setGradeUpdateSwitchListener(){
        this.notifications_new_grades_switch.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            settings.gradeNotificationsEnabled = b
        }
    }

    private fun setLectureNotificationSwitchListener(){
        this.current_lecture_notification_switch.setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
            settings.persistentLectureNotificationEnabled = b
        }
    }


    /**
     * Assigns a simple listener to the theme spinner to recreate the activity with fade
     * animations on theme change.
     */
    private fun setSpinnerListener(){
        this.current_theme_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // should never happen..
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position != settings.currentThemePos){
                    println("New theme selected, recreating the activity!")
                    /**
                     * When a different theme is selected, the activity must be recreated, to reflect the changes
                     * of the theme, so the actual theme selection happens in the main activity: after onCreate, but before SetContentView.
                     */
                    settings.currentThemePos = position
                    val intent = activity!!.intent
                    activity!!.finish()
                    activity!!.startActivity(intent)
//                    activity!!.recreate()
                    // Check if we're running on Android 5.0 or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        println("Animations Overridden!")
                    } else {

                    }

                }
            }
        }

    }

}
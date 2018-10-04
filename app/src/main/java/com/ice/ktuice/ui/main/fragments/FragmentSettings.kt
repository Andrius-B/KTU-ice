package com.ice.ktuice.ui.main.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.ice.ktuice.DAL.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class FragmentSettings: Fragment(), KoinComponent, AnkoLogger{

    val preferenceRepository: PreferenceRepository by inject()


    var currentThemePos: Int
        get(){
            try{
                return preferenceRepository.getValue(context!!.getString(R.string.currently_selected_theme_position)).toInt()
            }catch (e: NumberFormatException){
                warn("Wrong theme set! Resetting..")
                preferenceRepository.setValue(context!!.getString(R.string.currently_selected_theme_position), "0")
            }
            return 0
        }
        set(value){
            preferenceRepository.setValue(context!!.getString(R.string.currently_selected_theme_position), value.toString())
        }

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
        this.current_theme_spinner.setSelection(currentThemePos)
        this.current_theme_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // should never happen..
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position != currentThemePos){
                    println("New theme selected, recreating the activity!")
                    /**
                     * When a different theme is selected, the activity must be recreated, to reflect the changes
                     * of the theme, so the actual theme selection happens in the main activity: after onCreate, but before SetContentView.
                     */
                    currentThemePos = position
                    activity!!.recreate()
                }
            }
        }
    }

}
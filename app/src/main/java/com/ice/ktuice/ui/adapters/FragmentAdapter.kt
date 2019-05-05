package com.ice.ktuice.ui.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.ice.ktuice.repositories.prefrenceRepository.PreferenceRepository
import com.ice.ktuice.R
import com.ice.ktuice.ui.main.fragments.FragmentGrades
import com.ice.ktuice.ui.main.fragments.FragmentSettings
import com.ice.ktuice.ui.main.fragments.FragmentTimeTable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Created by Andrius on 2/24/2018.
 * Fragments, that the tab layout can manage
 */
class FragmentAdapter(fm: FragmentManager, context: Context): FragmentStatePagerAdapter(fm), KoinComponent{
    private val preferenceRepository: PreferenceRepository by inject()

    override fun getItem(position: Int): Fragment {
        if(position == 0){
          return FragmentGrades()
        }
        else if(position == 1){
          return FragmentTimeTable()
        }else {
          return  FragmentSettings()
        }
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        preferenceRepository.setValue(R.string.currently_selected_tab_id, position.toString())
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getCount() = 3

    private val titles = arrayListOf(context.getString(R.string.tab_name_grades), context.getString(R.string.tab_name_timetable), context.getString(R.string.tab_name_settings))
    override fun getPageTitle(position: Int) = titles[position]

}
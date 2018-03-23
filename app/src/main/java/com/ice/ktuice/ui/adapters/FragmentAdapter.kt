package com.ice.ktuice.ui.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.ice.ktuice.R
import com.ice.ktuice.ui.main.fragments.FragmentGrades
import com.ice.ktuice.ui.main.fragments.FragmentTimeTable

/**
 * Created by Andrius on 2/24/2018.
 * Fragments, that the tab layout can manage
 */
class FragmentAdapter(fm: FragmentManager, context: Context): FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if(position == 0) FragmentGrades()
               else FragmentTimeTable()

    }

    override fun getCount() = 2

    private val titles = arrayListOf(context.getString(R.string.tab_name_grades), context.getString(R.string.tab_name_timetable))
    override fun getPageTitle(position: Int) = titles[position]

}
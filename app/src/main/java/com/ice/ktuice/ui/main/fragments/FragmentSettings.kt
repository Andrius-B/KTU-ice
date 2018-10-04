package com.ice.ktuice.ui.main.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.ice.ktuice.R
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.koin.standalone.KoinComponent

class FragmentSettings: Fragment(), KoinComponent, AnkoLogger{

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
        this.current_theme_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // should never happen..
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                info("Selected theme position:$position")
            }
        }
    }

}
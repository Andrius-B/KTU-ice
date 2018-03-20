package com.ice.ktuice.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ice.ktuice.al.GradeTable.gradeTableModels.SemesterAdapterItem
import com.ice.ktuice.R

/**
 * Created by Andrius on 2/15/2018.
 */
class SemesterSpinnerAdapter(val context: Context,private val itemList: List<SemesterAdapterItem>): BaseAdapter() {

    override fun getView(index: Int, recycleView: View?, parent: ViewGroup?): View {
        val model = itemList[index]
        val view = if(recycleView == null){
            LayoutInflater.from(context).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
        }else{
            recycleView
        }

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = String.format("%s %s", model.year.year, model.semester)

        return view
    }

    override fun getItem(index: Int) = itemList[index]

    override fun getItemId(index: Int) = index.toLong()

    override fun getCount() = itemList.size

}
package com.ice.ktuice.UI.main.components

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.ice.ktuice.AL.GradeTableRowModel.GradeTableModel

/**
 * Created by Andrius on 1/29/2018.
 */
class GradeTable(c: Context, attrs: AttributeSet?): LinearLayout(c, attrs){
    constructor(c: Context): this(c, null)

    fun createViewForModel(gradeTableModel: GradeTableModel){
        this.removeAllViews()
        val tableLayout = TableLayout(context)

        val rowModelList = gradeTableModel.getRows()
        val weekModelList = gradeTableModel.getTotalWeekList()

        //header generation
        val tableRow = TableRow(context)
        val header = TextView(context)
        header.text = "Week"
        tableRow.addView(header)
        weekModelList.forEach {
            val weekText = TextView(context)
            weekText.text = it.week
            tableRow.addView(weekText)
        }
        tableLayout.addView(tableRow)

        rowModelList.forEach{
            val tableRow = TableRow(context)
            val moduleName = it.moduleModel.module_name
            val moduleNameText = TextView(context)
            moduleNameText.text = moduleName
            tableRow.addView(moduleNameText)

            weekModelList.forEach{weekModel ->
                val markCell = TextView(context)
                markCell.text = it.getStringByWeekModel(weekModel)
                tableRow.addView(markCell)
            }
            tableLayout.addView(tableRow)
        }
        this.addView(tableLayout)
    }

}
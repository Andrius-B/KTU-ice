package com.ice.ktuice.UI.main.components

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ice.ktuice.AL.GradeTableRowModel.GradeTableModel
import com.ice.ktuice.R
import kotlinx.android.synthetic.main.grade_table_layout.view.*

/**
 * Created by Andrius on 1/29/2018.
 */
class GradeTable(c: Context, attrs: AttributeSet?): LinearLayout(c, attrs){
    constructor(c: Context): this(c, null)

    init {
        inflate(context, R.layout.grade_table_layout, this)
    }

    fun createViewForModel(gradeTableModel: GradeTableModel){
        val rowModelList = gradeTableModel.getRows()
        val weekModelList = gradeTableModel.getTotalWeekList()

        //header generation
        val tableRow = TableRow(context)
        val header = TextView(context)
        //header.text  = context.getText(R.string.grade_table_week_header)
        //tableRow.addView(header)
        weekModelList.forEach {
            val weekText = TextView(context)
            weekText.text = it.week
            tableRow.addView(weekText)
        }
        grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))


        grade_table_headers.addView(TextView(context)) // add a spacer dummy text
        rowModelList.forEach{
            val tableRow = TableRow(context)
            val moduleName = it.moduleModel.module_name
            val moduleNameText = TextView(context)
            moduleNameText.text = moduleName
            moduleNameText.setSingleLine(true)
            moduleNameText.ellipsize = TextUtils.TruncateAt.END
            moduleNameText.maxLines = 1

            grade_table_headers.addView(moduleNameText)

            weekModelList.forEach{weekModel ->

                val markCellText = TextView(context)
                markCellText.setPadding(6,0,6,0)
                val cellModel = it.getByWeekModel(weekModel)
                markCellText.text = cellModel?.getDisplayString() ?: "" // default is empty cell
                tableRow.addView(markCellText)
            }
            grade_table_table_layout.addView(tableRow, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

            grade_table_table_layout.requestLayout()
            grade_table_grade_scroll_view.requestLayout()
            grade_table_grade_scroll_view_content.requestLayout()
            requestLayout()
        }
    }

}
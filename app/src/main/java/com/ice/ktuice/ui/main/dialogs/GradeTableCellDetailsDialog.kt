package com.ice.ktuice.ui.main.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.ice.ktuice.al.GradeTable.gradeTableModels.GradeTableCellModel
import com.ice.ktuice.R
import kotlinx.android.synthetic.main.dialog_mark_cell_details.*

/**
 * Created by Andrius on 2/9/2018.
 */
class GradeTableCellDetailsDialog(ctx: Context): Dialog(ctx) {
    var CellModel:GradeTableCellModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutInflater.inflate(R.layout.dialog_mark_cell_details, null))
    }

    override fun onStart() {
        super.onStart()
        if(CellModel != null){
            setForCellModel(CellModel!!)
            item_detail_table.visibility = View.VISIBLE
            no_grades_found_text.visibility = View.GONE
        }else{
            item_detail_table.visibility = View.GONE
            no_grades_found_text.visibility = View.VISIBLE
        }
    }

    private fun setForCellModel(cellModel: GradeTableCellModel){
        val mark = cellModel.gradeModels.firstOrNull() ?: throw NullPointerException("Can not open the dialog for an empty cell!")
        module_code_text.text = mark.module_code
        module_name_text.text = mark.module_name
        mark_text.text = mark.marks.first()
        past_marks_text.text = mark.marks.elementAtOrNull(1)
    }
}
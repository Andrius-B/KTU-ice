package com.ice.ktuice.al.services.yearGradesService

import com.ice.ktuice.models.YearGradesCollectionModel
import java.security.MessageDigest
import java.util.*

/**
 * It seems that Realm database sometimes fetches objects incorrectly and they must be validated before use
 * (i.e. returns YearGradeModels with zero modules/marks to track this issue down,
 * some validation information will be embedded into the models)
 */
class YearGradesCollectionModelValidator {

    class ValidationInformation(
        val valid: Boolean,
        val markCnt: Int,
        val moduleCnt: Int,
        val semesterCnt: Int,
        val yearCnt: Int,
        val htmlHash: String
    )

    /**
     * This function will assume that the passed YearGradesModel is correct
     * and the source of truth (presumably fresh from the web)
     * and will add some validation information such as
     *  * module count
     *  * mark count
     *  * html hash
     */
    fun addValidationInformation(marks: YearGradesCollectionModel){
        marks.forEach { yearGradesModel ->
            marks.yearCnt++
            yearGradesModel.semesterList.forEach{ semesterModel ->
                marks.semesterCnt++
                semesterModel.moduleList.forEach{ moduleModel ->
                    marks.moduleCnt++
                    moduleModel.grades.forEach{
                        it.marks.forEach{ _ ->
                            marks.markCnt++
                        }
                    }
                }
            }
        }

        marks.htmlHash = hashString(marks.rawHtml)
//        marks.rawHtml = "" // delete the raw HTML
    }

    fun validateModel(marks: YearGradesCollectionModel): ValidationInformation{
        var markCnt = 0
        var moduleCnt = 0
        var semesterCnt = 0
        var yearCnt = 0
        marks.forEach { yearGradesModel ->
            yearCnt++
            yearGradesModel.semesterList.forEach{ semesterModel ->
                semesterCnt++
                semesterModel.moduleList.forEach{ moduleModel ->
                    moduleCnt++
                    moduleModel.grades.forEach{
                        it.marks.forEach{ _ ->
                            markCnt++
                        }
                    }
                }
            }
        }

        val htmlHash = hashString(marks.rawHtml)

        val countsEq =  (markCnt     ==      marks.markCnt)       and
                        (moduleCnt   ==      marks.moduleCnt)     and
                        (semesterCnt ==      marks.semesterCnt)   and
                        (yearCnt     ==      marks.yearCnt)
        val hashesEq = marks.htmlHash == htmlHash


        return  ValidationInformation(
                countsEq and hashesEq,
                markCnt,
                moduleCnt,
                semesterCnt,
                yearCnt,
                htmlHash
                )
    }

    fun hashString(message: String): String{
        val bytes = MessageDigest.getInstance("SHA-1").digest(message.toByteArray())
        val HEX_CHARS = "0123456789ABCDEF"
        val resultStr = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            //shr - shift right
            resultStr.append(HEX_CHARS[i shr 4 and 0x0f])
            resultStr.append(HEX_CHARS[i and 0x0f])
        }
        return resultStr.toString()
    }
}
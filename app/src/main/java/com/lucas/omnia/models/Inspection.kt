package com.lucas.omnia.models

import android.content.res.Resources
import com.lucas.omnia.R
import com.lucas.omnia.activities.*
import java.util.*

data class Inspection(var name: String? = null, var activityClass: Class<*>? = null) {

    companion object {
        @JvmStatic
        fun createInspectionList(resources: Resources): ArrayList<Inspection> {
            val inspectionList = ArrayList<Inspection>()
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_1), BlocksActivity::class.java))
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_9), BodiesActivity::class.java))
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_2), DeputiesActivity::class.java))
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_3), EventsActivity::class.java))
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_4), FrontsActivity::class.java))
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_6), PartiesActivity::class.java))
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_7), PropositionsActivity::class.java))
            inspectionList.add(Inspection(resources.getString(R.string.inspection_item_8), PollsActivity::class.java))
            return inspectionList
        }
    }
}
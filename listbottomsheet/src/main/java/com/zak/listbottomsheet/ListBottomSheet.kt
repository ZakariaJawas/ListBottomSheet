package com.zak.listbottomsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zak.listbottomsheet.adapter.ListAdapter

class ListBottomSheet<T : Any>(
    private val mContext: Context,
    private val title: String,
    private val mList: List<T>,
    val onChooseItem: (ListBottomSheet<T>, T, Int) -> Unit
) : BottomSheetDialog(mContext) {

    var mAdapter: ListAdapter? = null
    var recyclerView: RecyclerView? = null
    var bottomSheetView: View = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_list_layout, null)

    init {

        this.setContentView(bottomSheetView)

        bottomSheetView.findViewById<TextView>(R.id.lblTitle).text = title

        //get the NameField values from the list
        var fieldValue = ""
        val valuesList: List<String> = mList.map {

            //get the value of @NameField
            for (field in it::class.java.declaredFields) {
                field.isAccessible = true
                if (field.getAnnotation(NameField::class.java) != null) {
                    fieldValue = field.get(it).toString()
                    break
                } //end if
            } //end for
            fieldValue
        }

        mAdapter = ListAdapter(mContext, valuesList) { _, position ->

            onChooseItem(this, mList[position], position)
        }

        recyclerView = bottomSheetView.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(mContext)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = mAdapter
    }

}
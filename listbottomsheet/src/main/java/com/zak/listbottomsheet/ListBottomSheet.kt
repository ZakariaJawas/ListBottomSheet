package com.zak.listbottomsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zak.listbottomsheet.adapter.ListAdapter
import com.zak.listbottomsheet.model.ListBottomSheetModel

class ListBottomSheet(private val mContext: Context, private val title: String, private val mList: List<ListBottomSheetModel>, val onChooseItem: (ListBottomSheet, ListBottomSheetModel, Int) -> Unit) : BottomSheetDialog(mContext) {

    var mAdapter: ListAdapter? = null
    var recyclerView: RecyclerView? = null
    var bottomSheetView: View = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_list_layout, null)

    init {

        this.setContentView(bottomSheetView)

        bottomSheetView.findViewById<TextView>(R.id.lblTitle).text = title
        mAdapter = ListAdapter(mContext, mList.toMutableList()) { item, position ->

            onChooseItem(this, item, position)
        }

        recyclerView = bottomSheetView.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(mContext)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = mAdapter
    }

}
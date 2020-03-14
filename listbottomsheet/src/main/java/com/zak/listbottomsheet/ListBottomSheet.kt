package com.zak.listbottomsheet

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zak.listbottomsheet.adapter.ListAdapter

class ListBottomSheet<T : Any> private constructor(
    private val mContext: Context,
    private val _title: String,
    private val mList: List<T>?,
    private val layoutResource: Int = R.layout.bottom_sheet_list_item,
    private val onChooseItem: ((ListBottomSheet<T>, T, Int) -> Unit)?
) : BottomSheetDialog(mContext) {

    class Builder<T : Any>(private val mContext: Context) {


        private lateinit var title: String
        private lateinit var mList: List<T>
        private var layoutResource: Int = R.layout.bottom_sheet_list_item
        private var onChooseItem: ((ListBottomSheet<T>, T, Int) -> Unit)? = null

        fun title(title: String) = apply { this.title = title }
        fun list(mList: List<T>) = apply { this.mList = mList }
        fun itemLayout(layoutResource: Int) = apply { this.layoutResource = layoutResource }
        fun onChooseItemCallback(onChooseItem: (ListBottomSheet<T>, T, Int) -> Unit) =
            apply { this.onChooseItem = onChooseItem }

        fun build() = ListBottomSheet(mContext, title, mList, layoutResource, onChooseItem)
    }

    private var mAdapter: ListAdapter? = null
    var recyclerView: RecyclerView? = null
    var bottomSheetView: View = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_list_layout, null)

    var title: String = _title
        set(value) {
            bottomSheetView.findViewById<TextView>(R.id.lblTitle).text = value
            field = value
        }

    var titleColor: Int = bottomSheetView.findViewById<TextView>(R.id.lblTitle).currentTextColor
        set(value) {
            bottomSheetView.findViewById<TextView>(R.id.lblTitle).setTextColor(value)
            field = value
        }

    var titleSize: Float = bottomSheetView.findViewById<TextView>(R.id.lblTitle).textSize
        set(value) {
            bottomSheetView.findViewById<TextView>(R.id.lblTitle)
                .setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
            field = value
        }

    var titleAlignment: Int = bottomSheetView.findViewById<TextView>(R.id.lblTitle).gravity
        set(value) {
            bottomSheetView.findViewById<TextView>(R.id.lblTitle).gravity = value
            field = value
        }

    init {
        bottomSheetView.findViewById<TextView>(R.id.lblTitle).text = _title
        this.setContentView(bottomSheetView)

        //get the @NameField values from the list
        var fieldValue = ""
        var isNameFieldFound = false

        val valuesList: List<String> = mList!!.map {
            //get the value of @NameField
            it::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                if (field.getAnnotation(NameField::class.java) != null) {
                    fieldValue = field.get(it).toString()
                    isNameFieldFound = true
                    return@forEach
                }//end if
            } //end for

            if (!isNameFieldFound) {
                throw UnspecifiedNameFieldException("Please annotate your title field in your model class (${mList[0]::class.java.simpleName}) with @NameField")
            } //end if

            fieldValue
        }


        mAdapter = ListAdapter(mContext, valuesList, layoutResource) { _, position ->

            onChooseItem?.also {
                it(this, mList[position], position)
            }
        }

        recyclerView = bottomSheetView.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(mContext)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = mAdapter
    }
}
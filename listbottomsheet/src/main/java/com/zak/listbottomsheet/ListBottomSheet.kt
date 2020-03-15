package com.zak.listbottomsheet

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zak.listbottomsheet.adapter.ListAdapter
import kotlinx.android.synthetic.main.bottom_sheet_list_layout.view.*

/**
 * ListBottomSheet class
 * a class that displays list of items as a bottom sheet
 * @param T the type of the model that will be returned by the sheet
 * @builder used to build the sheet
 * @property title the title of the sheet
 * @property titleColor the color of the title
 * @property titleSize the size of the title
 * @property titleAlignment the alignment of the sheet title
 */
class ListBottomSheet<T : Any> private constructor(
    private val mContext: Context,
    private val _title: String,
    private val mList: List<T>?,
    private val layoutResource: Int = R.layout.bottom_sheet_list_item,
    private val cancelable: Boolean = true,
    private var cancelButtonVisibility: Boolean = false,
    private val onChooseItem: ((ListBottomSheet<T>, T, Int) -> Unit)?
) : BottomSheetDialog(mContext) {

    class Builder<T : Any>(private val mContext: Context) {


        private lateinit var title: String
        private lateinit var mList: List<T>
        private var layoutResource: Int = R.layout.bottom_sheet_list_item
        private var onChooseItem: ((ListBottomSheet<T>, T, Int) -> Unit)? = null
        private var cancelable: Boolean = true
        private var cancelButtonVisibility: Boolean = false


        fun title(title: String) = apply { this.title = title }
        fun list(mList: List<T>) = apply { this.mList = mList }
        fun itemLayout(layoutResource: Int) = apply { this.layoutResource = layoutResource }
        fun onChooseItemCallback(onChooseItem: (ListBottomSheet<T>, T, Int) -> Unit) =
            apply { this.onChooseItem = onChooseItem }
        fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun cancelButtonVisible(cancelButtonVisibility: Boolean) = apply { this.cancelButtonVisibility = cancelButtonVisibility }
        fun build() = ListBottomSheet(mContext, title, mList, layoutResource, cancelable, cancelButtonVisibility, onChooseItem)
    }

    private var mAdapter: ListAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var bottomSheetView: View = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_list_layout, null)

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
        setCancelable(cancelable)

        //get the @NameField values from the list
        var fieldValue = "" //used to get the value from T model
        var isNameFieldFound = false //found indicator

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

        //hide cancel button if false
        if (cancelButtonVisibility) {
            bottomSheetView.findViewById<ImageView>(R.id.imgCancel).setOnClickListener {
                dismiss()
            }
        } else {
            bottomSheetView.findViewById<ImageView>(R.id.imgCancel).visibility = View.GONE
        }//end if


        //fill the adapter
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
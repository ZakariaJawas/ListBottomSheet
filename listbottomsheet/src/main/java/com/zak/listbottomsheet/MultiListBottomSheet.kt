package com.zak.listbottomsheet

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zak.listbottomsheet.adapter.MultiListAdapter
//import kotlinx.android.synthetic.main.bottom_sheet_list_layout.view.*

/**
 * ListBottomSheet class
 * a class that displays list of items as a bottom sheet
 * @param T type of the model that will be returned by the sheet
 * @builder used to build the sheet
 * @property title the title of the sheet
 * @property titleColor the color of the title
 * @property titleSize the size of the title
 * @property titleAlignment the alignment of the sheet title
 */
class MultiListBottomSheet<T : Any> private constructor(
    private val mContext: Context,
    private val _title: String,
    private val mList: List<T>?,
    private val layoutResource: Int = R.layout.bottom_sheet_list_item,
    private val cancelable: Boolean,
    private var cancelButtonVisibility: Boolean,
    private val searchable: Boolean,
    private val onChooseItem: ((MultiListBottomSheet<T>, T, Int) -> Unit)?,
    private val selectedItemColor: Int,
    private val selectedItemBackgroundColor: Int,
    private val onActionCallback: ((MultiListBottomSheet<T>, List<T>) -> Unit)?,
    private val actionButtonTitle: String?,
    private val searchHint: String?,
    private val customTypeface: Typeface?
) : BottomSheetDialog(mContext) {

    class Builder<T : Any>(private val mContext: Context) {

        private lateinit var title: String
        private lateinit var mList: List<T>
        private var layoutResource: Int = R.layout.bottom_sheet_list_item
        private var onChooseItem: ((MultiListBottomSheet<T>, T, Int) -> Unit)? = null
        private var cancelable: Boolean = true
        private var cancelButtonVisibility: Boolean = false
        private var searchable: Boolean = false
        private var selectedItemColor: Int = Color.BLACK //black color
        private var selectedItemBackgroundColor: Int = Color.LTGRAY
        private var onActionCallback: ((MultiListBottomSheet<T>, List<T>) -> Unit)? = null
        private var actionButtonTitle: String = ""
        private var searchHint: String = "Search"
        private var customTypeface: Typeface? = null

        fun title(title: String) = apply { this.title = title }
        fun list(mList: List<T>) = apply { this.mList = mList }
        fun itemLayout(layoutResource: Int) = apply { this.layoutResource = layoutResource }
        fun onChooseItemCallback(onChooseItem: (MultiListBottomSheet<T>, T, Int) -> Unit) =
            apply { this.onChooseItem = onChooseItem }
        fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun cancelButtonVisible(cancelButtonVisibility: Boolean) = apply { this.cancelButtonVisibility = cancelButtonVisibility }
        fun searchable(searchable: Boolean) = apply { this.searchable = searchable }


        fun selectedItemColor(selectedItemColor: Int) =
            apply { this.selectedItemColor = selectedItemColor }

        fun selectedItemBackgroundColor(selectedItemBackgroundColor: Int) =
            apply { this.selectedItemBackgroundColor = selectedItemBackgroundColor }

        fun setOnActionCallback(onActionCallback: (MultiListBottomSheet<T>, List<T>) -> Unit) = apply {
            this.onActionCallback = onActionCallback
        }

        fun setActionButtonTitle(actionButtonTitle: String) = apply {
            this.actionButtonTitle = actionButtonTitle
        }

        fun setSearchHint(hint: String) = apply {
            this.searchHint = hint
        }

        fun setCustomTypeface(typeface: Typeface) = apply {
            this.customTypeface = typeface
        }

        fun build() = MultiListBottomSheet(
            mContext,
            title,
            mList,
            layoutResource,
            cancelable,
            cancelButtonVisibility,
            searchable,
            onChooseItem,
            selectedItemColor,
            selectedItemBackgroundColor,
            onActionCallback,
            actionButtonTitle,
            searchHint,
            customTypeface
        )
    }

    private var mAdapter: MultiListAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var txtSearch: EditText? = null
    private var btnAction: Button? = null
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
        setSearchable(searchable)
        //get the @NameField values from the list
        var fieldValue = "" //used to get the value from T model
        var isNameFieldFound = false //found indicator

        val valuesList: List<ListItem> = mList!!.withIndex().map {
            //get the value of @NameField
            it.value::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                if (field.getAnnotation(NameField::class.java) != null) {
                    fieldValue = field.get(it.value).toString()
                    isNameFieldFound = true
                    return@forEach
                }//end if
            } //end for

            if (!isNameFieldFound) {
                throw UnspecifiedNameFieldException("Please annotate your title field in your model class (${mList[0]::class.java.simpleName}) with @NameField")
            } //end if

            ListItem(it.index, fieldValue)
        }

        //hide cancel button if false
        if (cancelButtonVisibility) {
            bottomSheetView.findViewById<ImageView>(R.id.imgCancel).setOnClickListener {
                dismiss()
            }
        } else {
            bottomSheetView.findViewById<ImageView>(R.id.imgCancel).visibility = View.GONE
        }//end if


        val textView =  LayoutInflater.from(context).inflate(layoutResource, null)
            .findViewById<TextView>(R.id.textView)

        val defaultItemColor = textView.currentTextColor
        val defaultItemBackgroundColor = (textView.background as? ColorDrawable)?.color ?: Color.parseColor("#00000000")

        //fill the adapter
        mAdapter = MultiListAdapter(
            mContext,
            valuesList.toMutableList(),
            layoutResource,
            defaultItemColor,
            selectedItemColor,
            selectedItemBackgroundColor,
            defaultItemBackgroundColor,
            customTypeface
        ) { item ->

            onChooseItem?.also {
                it(this, mList[item.position], item.position)
            }
        }

        recyclerView = bottomSheetView.findViewById(R.id.recyclerView)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(mContext)
            setHasFixedSize(true)
            adapter = mAdapter
        }

        txtSearch = bottomSheetView.findViewById(R.id.txtSearch)
        btnAction = bottomSheetView.findViewById(R.id.btnAction)

//        setSelectedItem()

        onActionCallback?.also { callback ->
            btnAction?.apply {
                text = actionButtonTitle ?: "OK"
                visibility = View.VISIBLE
                setOnClickListener {
                    //return list of the selected items
                    val selectedItems = (recyclerView?.adapter as MultiListAdapter).selectedItemsList.toList()
                    Log.d("##items in sheet", "size ${selectedItems.size}")
                    val resultList = mutableListOf<T>()
                    repeat(selectedItems.size) {
                        resultList.add(mList[it])
                    }
                    callback(this@MultiListBottomSheet, resultList)
                }
            }
        }

        txtSearch?.hint = searchHint
        customTypeface?.also {
            txtSearch?.typeface = it
            btnAction?.typeface = it
        }
    }

    private fun setSearchable(searchable: Boolean) {

        if (!searchable) {
            return
        } //end if

        //display the search view
        bottomSheetView.findViewById<View>(R.id.searchContainer).visibility = View.VISIBLE

        //bind txtSearch watcher listener
        bottomSheetView.findViewById<EditText>(R.id.txtSearch).addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

                mAdapter?.filter?.filter(charSequence)
            }
        })
    }

    override fun show() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) //hide keyboard when the sheet is open

        Handler(Looper.getMainLooper()).postDelayed({

            val d = this as BottomSheetDialog
            val bottomSheet = d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

        }, 0)
        super.show()
    }

    fun clearSelectedItems() {
        (recyclerView?.adapter as? MultiListAdapter)?.also {
            it.selectedItemsList.clear()
            it.notifyDataSetChanged()
        }
    }
}
package com.zak.listbottomsheet
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zak.listbottomsheet.adapter.ListAdapter

/**
 * ListBottomSheet class
 * a class that displays list of items as a bottom sheet
 * @param T type of the model that will be returned by the sheet
 * @builder used to build the sheet
 * @property title the title of the sheet
 * @property titleColor the color of the title
 * @property titleSize the size of the title
 * @property titleAlignment the alignment of the sheet title
 * @property selectedItemIndex the current selected item of the list
 */
class ListBottomSheet<T : Any> private constructor(
    private val mContext: Context,
    private val _title: String,
    private val mList: List<T>?,
    private val layoutResource: Int = R.layout.bottom_sheet_list_item,
    private val cancelable: Boolean,
    private var cancelButtonVisibility: Boolean,
    private val searchable: Boolean,
    private var _selectedItemIndex: Int,
    private val onChooseItem: ((ListBottomSheet<T>, T, Int) -> Unit)?,
    private val selectedItemColor: Int,
    private val selectedItemBackColor: Int,
    private val onActionCallback: ((ListBottomSheet<T>) -> Unit)?,
    private val actionButtonTitle: String?,
    private val searchHint: String?,
    private val customTypeface: Typeface?
) : BottomSheetDialog(mContext) {

    class Builder<T : Any>(private val mContext: Context) {

        private lateinit var title: String
        private lateinit var mList: List<T>
        private var layoutResource: Int = R.layout.bottom_sheet_list_item
        private var onChooseItem: ((ListBottomSheet<T>, T, Int) -> Unit)? = null
        private var cancelable: Boolean = true
        private var cancelButtonVisibility: Boolean = false
        private var searchable: Boolean = false
        private var selectedItemIndex: Int = -1 //no selected item as default
        private var selectedItemColor: Int = Color.BLACK //black color
        private var selectedItemBackColor: Int = Color.LTGRAY
        private var onActionCallback: ((ListBottomSheet<T>) -> Unit)? = null
        private var actionButtonTitle: String = ""
        private var searchHint: String = "Search"
        private var customTypeface: Typeface? = null

        fun title(title: String) = apply { this.title = title }
        fun list(mList: List<T>) = apply { this.mList = mList }
        fun itemLayout(layoutResource: Int) = apply { this.layoutResource = layoutResource }
        fun onChooseItemCallback(onChooseItem: (ListBottomSheet<T>, T, Int) -> Unit) =
            apply { this.onChooseItem = onChooseItem }
        fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun cancelButtonVisible(cancelButtonVisibility: Boolean) = apply { this.cancelButtonVisibility = cancelButtonVisibility }
        fun searchable(searchable: Boolean) = apply { this.searchable = searchable }
        fun selectedItemIndex(selectedItemIndex: Int) =
            apply { this.selectedItemIndex = selectedItemIndex }

        fun selectedItemColor(selectedItemColor: Int) =
            apply { this.selectedItemColor = selectedItemColor }

        fun setOnActionCallback(onActionCallback: (ListBottomSheet<T>) -> Unit) = apply {
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

        fun build() = ListBottomSheet(
            mContext,
            title,
            mList,
            layoutResource,
            cancelable,
            cancelButtonVisibility,
            searchable,
            selectedItemIndex,
            onChooseItem,
            selectedItemColor,
            selectedItemBackColor,
            onActionCallback,
            actionButtonTitle,
            searchHint,
            customTypeface
        )
    }

    private var mAdapter: ListAdapter? = null
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

    var selectedItemIndex: Int = _selectedItemIndex
        set(value) {
            mAdapter?.selectedItem = value
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


        val defaultItemColor = LayoutInflater.from(context).inflate(layoutResource, null)
            .findViewById<TextView>(R.id.textView).currentTextColor

        //fill the adapter
        mAdapter = ListAdapter(
            mContext,
            valuesList.toMutableList(),
            layoutResource,
            defaultItemColor,
            selectedItemColor,
            customTypeface
        ) { item ->

            onChooseItem?.also {
                selectedItemIndex = item.position
                setSelectedItem()
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

        setSelectedItem()

        onActionCallback?.also { callback ->
            bottomSheetView.findViewById<Button>(R.id.btnAction).apply {
                text = actionButtonTitle ?: "OK"
                visibility = View.VISIBLE
                setOnClickListener {
                    callback(this@ListBottomSheet)
                }
            }
        }

        txtSearch?.hint = searchHint
        customTypeface?.also {
            txtSearch?.typeface = it
            btnAction?.typeface = it
        }
    }

    private fun setSelectedItem() {
        mAdapter?.selectedItem = this.selectedItemIndex
    }

    private fun setSearchable(searchable: Boolean) {

        if (!searchable) {
            return
        }

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
}
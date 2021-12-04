package com.zak.listbottomsheetproject

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zak.listbottomsheet.ListBottomSheet
import com.zak.listbottomsheet.MultiListBottomSheet
import com.zak.listbottomsheet.NameField
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val typeface = Typeface.createFromAsset(applicationContext.assets, "font/roboto_bold.ttf")

        val mList = listOf(Category(1, "Category 1"), Category(2, "Category 2"), Category(3, "Category 3"))
        val listSheet = ListBottomSheet.Builder<Category>(this)
            .list(mList)
            .title("Choose one")
//          .itemLayout(R.layout.custom_list_item) //to set a custom layout for list items instead of the default one
            .onChooseItemCallback { sheet: ListBottomSheet<Category>, category: Category, position: Int ->
                sheet.dismiss() //hide the dialog
                Log.d("##chosen_cat", "chosen category is ${category.name} at position $position")
            }
            .cancelable(false) //prevent the sheet from being canceled by clicking outside the sheet
            .cancelButtonVisible(true) //show the cancel button
            .searchable(true)
//          .selectedItemColor(ContextCompat.getColor(this, R.color.colorAccent)) //set the selected item color
//          .selectedItemIndex(0) //initial the list with selected item
            .setActionButtonTitle("Continue")
            .setOnActionCallback {
                Toast.makeText(this, "On Action Button Clicked", Toast.LENGTH_SHORT).show()
                it.dismiss()
            }
//            .setSearchHint("Search here") //change search text hint
//            .setCustomTypeface(typeface) //change list items font
            .build()

//        listSheet.titleAlignment = Gravity.RIGHT //to change title text alignment
//        listSheet.titleSize = 18F //to change title text size
//        listSheet.titleColor = ContextCompat.getColor(this, R.color.colorPrimary) //to change title text color
//        listSheet.selectedItemIndex = 1 //to change the selected item after you build the sheet

        btnOpenSheet.setOnClickListener {

            listSheet.show()
        }

        val multiListSheet = MultiListBottomSheet.Builder<Category>(this)
            .list(mList)
            .title("Choose categories")
//          .itemLayout(R.layout.custom_list_item) //to set a custom layout for list items instead of the default one
            .onChooseItemCallback { sheet, item, position ->
                Log.d("##chosen_cat", "chosen category is ${item.name} at position $position")
            }
            .cancelable(false) //prevent the sheet from being canceled by clicking outside the sheet
            .cancelButtonVisible(true) //show the cancel button
            .searchable(true)
//          .selectedItemColor(ContextCompat.getColor(this, R.color.colorAccent)) //set the selected item color
            .selectedItemBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey))
            .setActionButtonTitle("Continue")
            .setOnActionCallback {
                sheet, selectedItems ->
                Toast.makeText(this, "On Action Button Clicked With ${selectedItems.size} items selected", Toast.LENGTH_SHORT).show()
                sheet.dismiss()
            }
//            .setSearchHint("Search here") //change search text hint
//            .setCustomTypeface(typeface) //change list items font
            .build()

//        multiListSheet.titleAlignment = Gravity.RIGHT //to change title text alignment
//        multiListSheet.titleSize = 18F //to change title text size
//        multiListSheet.titleColor = ContextCompat.getColor(this, R.color.colorPrimary) //to change title text color
//        multiListSheet.selectedItemIndex = 1 //to change the selected item after you build the sheet

        btnOpenMultiSheet.setOnClickListener {

            multiListSheet.show()
        }

    }
}



data class Category(val id: Int, @NameField var name: String)

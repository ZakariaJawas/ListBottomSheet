package com.zak.listbottomsheetproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zak.listbottomsheet.ListBottomSheet
import com.zak.listbottomsheet.NameField
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
//            .setActionButtonTitle("Continue")
//            .setOnActionCallback {
//                Toast.makeText(this, "On Action Button Clicked", Toast.LENGTH_SHORT).show()
//                it.dismiss()
//            }
            .build()

//        listSheet.titleAlignment = Gravity.RIGHT //to change title text alignment
//        listSheet.titleSize = 18F //to change title text size
//        listSheet.titleColor = ContextCompat.getColor(this, R.color.colorPrimary) //to change title text color
//        listSheet.selectedItemIndex = 1 //to change the selected item after you build the sheet

        btnOpenSheet.setOnClickListener {

            listSheet.show()
        }
    }
}



data class Category(val id: Int, @NameField var name: String)

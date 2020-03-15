package com.zak.listbottomsheetproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zak.listbottomsheet.ListBottomSheet
import com.zak.listbottomsheet.NameField

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mList = listOf(Category(1, "Category 1"), Category(2, "Category 2"), Category(3, "Category 3"))
        val listSheet = ListBottomSheet.Builder<Category>(this)
            .list(mList)
            .title("Choose one")
//            .itemLayout(R.layout.custom_list_item) //to set a custom layout for list items instead of the default one
            .onChooseItemCallback { sheet: ListBottomSheet<Category>, category: Category, position: Int ->
                sheet.dismiss() //hide the dialog
            }
            .cancelable(false) //prevent the sheet from being canceled by clicking outside the sheet
            .cancelButtonVisible(true) //show the cancel button
            .build()

//        listSheet.titleAlignment = Gravity.RIGHT //to change title text alignment
//        listSheet.titleSize = 18F //to change title text size
//        listSheet.titleColor = ContextCompat.getColor(this, R.color.colorPrimary) //to change title text color

        listSheet.show()
    }
}



data class Category(val id: Int, @NameField var name: String)

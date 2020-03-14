package com.zak.listbottomsheetproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zak.listbottomsheet.ListBottomSheet
import com.zak.listbottomsheet.NameField

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mList = listOf(Category(1, "Cat1"), Category(2, "Cat2"), Category(3, "Cat3"))
        val listSheet = ListBottomSheet.Builder<Category>(this)
            .list(mList)
            .title("Choose one")
            .itemLayout(R.layout.custome_list_item)
            .onChooseItemCallback { sheet: ListBottomSheet<Category>, category: Category, position: Int ->
                sheet.dismiss() //hide the dialog


            }
            .build()

//        listSheet.titleAlignment = Gravity.RIGHT //to change title text alignment
//        listSheet.titleSize = 18F //to change title text size
//        listSheet.titleColor = ContextCompat.getColor(this, R.color.colorPrimary) //to change title text color

        listSheet.show()
    }
}

/*
    git tag -a 1.0 -m "v1.0"
    git push origin 1.0
*/

data class Category(val id: Int, @NameField var name: String)

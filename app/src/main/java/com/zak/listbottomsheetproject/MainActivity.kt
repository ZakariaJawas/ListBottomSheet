package com.zak.listbottomsheetproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zak.listbottomsheet.ListBottomSheet
import com.zak.listbottomsheet.model.ListBottomSheetModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mList = listOf(Category(1, "Cat1"), Category(2, "Cat2"), Category(3, "Cat3"))

        ListBottomSheet(this, "Choose One", mList) {
            sheet: ListBottomSheet, item: ListBottomSheetModel, _: Int ->

            sheet.dismiss()
            Toast.makeText(this, "Item ${item.name} is chosen", Toast.LENGTH_LONG).show()
        }.show()
    }
}

/*
    git tag -a 1.0 -m "v1.0"
    git push origin 1.0
*/

data class Category(val id: Int, override var name: String): ListBottomSheetModel

package com.zak.listbottomsheetproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

data class Category(val id: Int, override var name: String): ListBottomSheetModel

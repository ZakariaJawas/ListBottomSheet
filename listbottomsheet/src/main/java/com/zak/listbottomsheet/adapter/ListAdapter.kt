package com.zak.listbottomsheet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zak.listbottomsheet.R

class ListAdapter(
    private val mContext: Context,
    var mList: List<String>,
    val onChooseItem: (String, Int) -> Unit
) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.bottom_sheet_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = mList[position]
        holder.setOnClickListener(item, onChooseItem)
        holder.lblName.text = item

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lblName: TextView = itemView.findViewById(R.id.lblName)
        fun setOnClickListener(item: String, listener: (String, Int) -> Unit) {
            itemView.setOnClickListener {

                listener(item, adapterPosition)
            }
        }
    }
}
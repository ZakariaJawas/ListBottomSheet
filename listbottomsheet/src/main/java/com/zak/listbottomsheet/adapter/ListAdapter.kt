package com.zak.listbottomsheet.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zak.listbottomsheet.ListItem
import com.zak.listbottomsheet.R

class ListAdapter(
    private val mContext: Context,
    var mList: MutableList<ListItem>,
    private val layoutResource: Int,
    private val defaultItemColor: Int,
    private val selectedItemColor: Int,
    private val typeface: Typeface?,
    private val onChooseItem: (ListItem) -> Unit
) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>(), Filterable{


    var oldSelectedItem = -1
    var selectedItem: Int = -1
        set(value) {

            if (value == -1 || value >= mList.size) {
                return
            }

            if (oldSelectedItem != -1) {
                mList[oldSelectedItem].isSelected = false
                notifyItemChanged(oldSelectedItem)
            } //end if

            mList[value].isSelected = true
            oldSelectedItem = value
            notifyItemChanged(value)
        }

    var listFiltered = mList.toMutableList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(
            layoutResource,
            parent,
            false
        )
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return listFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listFiltered[position]
        holder.setOnClickListener(item, onChooseItem)
        holder.lblName.text = item.title

        if (item.isSelected) {

            holder.lblName.setTextColor(selectedItemColor)
//            holder.lblName.setTypeface(holder.lblName.typeface, Typeface.BOLD)
        } else {

            holder.lblName.setTextColor(defaultItemColor)
//            holder.lblName.typeface = Typeface.DEFAULT
        }

        typeface?.also {
            holder.lblName.typeface = it
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lblName: TextView = itemView.findViewById(R.id.textView)

        fun setOnClickListener(item: ListItem, listener: (ListItem) -> Unit) {
            itemView.setOnClickListener {

                listener(item)
            }
        }
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                Log.d("##charSeq", "search for $charSequence")
                listFiltered = if (charSequence!!.isEmpty()) {

                    mList
                } else {

                    mList.filter {
                        Log.d("##charSeq", "search in ${it.title}")
                        it.title.toLowerCase().contains(charSequence.toString().toLowerCase())
                    }.toMutableList()
                }

                val filterResults = FilterResults()
                filterResults.values = listFiltered
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                listFiltered = filterResults?.values as MutableList<ListItem>
                notifyDataSetChanged()
            }

        }
    }
}
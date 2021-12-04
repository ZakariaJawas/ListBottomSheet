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
import java.util.*


class MultiListAdapter(
    private val mContext: Context,
    var mList: MutableList<ListItem>,
    private val layoutResource: Int,
    private val defaultItemColor: Int,
    private val selectedItemColor: Int,
    private val selectedItemBackgroundColor: Int,
    private val defaultItemBackgroundColor: Int,
    private val typeface: Typeface?,
    private val onChooseItem: (ListItem) -> Unit
) : RecyclerView.Adapter<MultiListAdapter.ViewHolder>(), Filterable{

    val selectedItemsList = mutableListOf<ListItem>()
    var listFiltered = mList.toMutableList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(
            layoutResource,
            parent,
            false
        )

        return ViewHolder(view, selectedItemColor, defaultItemColor, selectedItemBackgroundColor, defaultItemBackgroundColor)
    }


    override fun getItemCount(): Int {
        return listFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listFiltered[position]
        holder.setOnClickListener(this, item, onChooseItem)
        holder.lblName.text = item.title
        holder.isSelected = selectedItemsList.indexOf(item) > -1

        typeface?.also {
            holder.lblName.typeface = it
        }
    }


    class ViewHolder(itemView: View,private val selectedColor: Int,
                     private val defaultItemColor: Int,
                     private val selectedItemBackgroundColor: Int,
                     private val defaultBackgroundColor: Int) : RecyclerView.ViewHolder(itemView) {

        var lblName: TextView = itemView.findViewById(R.id.textView)

        var isSelected: Boolean = false
            set(value) {

                if (value) {
                    lblName.setBackgroundColor(selectedItemBackgroundColor)
                    lblName.setTextColor(selectedColor)
                } else {
                    lblName.setTextColor(
                        defaultItemColor
                    )

                    lblName.setBackgroundColor(defaultBackgroundColor)
                } //end if
                field = value
            }


        fun setOnClickListener(instance: MultiListAdapter, item: ListItem, listener: (ListItem) -> Unit) {
            itemView.setOnClickListener {
                if (!instance.selectedItemsList.remove(item)) { //if remove return false means it doesn't exists so add it to the list
                    Log.d("##item", " will be added")
                    instance.selectedItemsList.add(item)
                } //end if
                Log.d("##item", " size ${instance.selectedItemsList.size}")
                instance.notifyItemChanged(adapterPosition)
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
                        it.title.lowercase(Locale.getDefault()).contains(charSequence.toString()
                            .lowercase(Locale.getDefault()))
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
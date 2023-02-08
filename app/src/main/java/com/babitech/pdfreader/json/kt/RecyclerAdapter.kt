package com.babitech.pdfreader.json.kt

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.babitech.pdfreader.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast

class RecyclerAdapter(private val context: Context, private val listRecyclerItem: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val date: TextView

        init {
            name = itemView.findViewById<View>(R.id.name) as TextView
            date = itemView.findViewById<View>(R.id.date) as TextView
            val nams = name.text.toString()
          itemView.setOnClickListener {
              Toast.makeText(context, "$nams", Toast.LENGTH_SHORT).show()
          }
        }
        
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return when (i) {
            TYPE -> {
                val layoutView = LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.list_item, viewGroup, false
                )
                ItemViewHolder(layoutView)
            }
            else -> {
                val layoutView = LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.list_item, viewGroup, false
                )
                ItemViewHolder(layoutView)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val viewType = getItemViewType(i)
        when (viewType) {
            TYPE -> {
                val itemViewHolder = viewHolder as ItemViewHolder
                val holidays = listRecyclerItem[i] as Holidays
                itemViewHolder.name.text = holidays.name
                itemViewHolder.date.text = holidays.date
            }
            else -> {
                val itemViewHolder = viewHolder as ItemViewHolder
                val holidays = listRecyclerItem[i] as Holidays
                itemViewHolder.name.text = holidays.name
                itemViewHolder.date.text = holidays.date
            }
        }
    }

    override fun getItemCount(): Int {
        return listRecyclerItem.size
    }

    companion object {
        private const val TYPE = 1
    }
}
package com.babitech.pdfreader

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.babitech.pdfreader.R

class pdf_viewholder2(itemview: View) : RecyclerView.ViewHolder(itemview) {
    var tvName: TextView
    var container: CardView

    init {
        tvName = itemview.findViewById(R.id.pdf_name)
        container = itemview.findViewById(R.id.container)
    }
}
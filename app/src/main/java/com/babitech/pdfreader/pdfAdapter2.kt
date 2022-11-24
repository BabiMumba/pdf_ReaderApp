package com.babitech.pdfreader

import android.content.Context
import com.babitech.pdfreader.Pdf_listener_file
import androidx.recyclerview.widget.RecyclerView
import com.babitech.pdfreader.pdf_viewholder
import android.view.ViewGroup
import android.view.LayoutInflater
import com.babitech.pdfreader.R
import java.io.File

class pdfAdapter2(
    private val context: Context,
    private val pdffiles: List<File>,
    private val pdf_listener_file: Pdf_listener_file
) : RecyclerView.Adapter<pdf_viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pdf_viewholder {
        return pdf_viewholder(
            LayoutInflater.from(context).inflate(R.layout.item_pdf, parent, false)
        )
    }

    override fun onBindViewHolder(holder: pdf_viewholder, position: Int) {
        holder.tvName.text = pdffiles[position].name
        holder.container.isSelected = true
        holder.container.setOnClickListener {
            pdf_listener_file.onSelected(
                pdffiles[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return pdffiles.size
    }
}
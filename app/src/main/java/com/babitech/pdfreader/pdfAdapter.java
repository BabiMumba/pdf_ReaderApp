package com.babitech.pdfreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;


public class pdfAdapter extends RecyclerView.Adapter<pdf_viewholder> {

    private Context context;
    private List<File> pdffiles;
    private Pdf_listener_file pdf_listener_file;

    public pdfAdapter(Context context, List<File> pdffiles,Pdf_listener_file pdf_listener_file) {
        this.context = context;
        this.pdffiles = pdffiles;
        this.pdf_listener_file = pdf_listener_file;
    }

    @NonNull
    @Override
    public pdf_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new pdf_viewholder(LayoutInflater.from(context).inflate(R.layout.item_pdf,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull pdf_viewholder holder, int position) {
        holder.tvName.setText(pdffiles.get(position).getName());
        holder.container.setSelected(true);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdf_listener_file.onSelected(pdffiles.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdffiles.size();
    }
}

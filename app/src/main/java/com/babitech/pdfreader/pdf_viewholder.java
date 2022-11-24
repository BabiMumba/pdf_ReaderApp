package com.babitech.pdfreader;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class pdf_viewholder extends RecyclerView.ViewHolder {
    public TextView tvName;
    public CardView container;
    public pdf_viewholder(@NonNull View itemview){
        super(itemview);
        tvName = itemview.findViewById(R.id.pdf_name);
        container = itemview.findViewById(R.id.container);
    }
}

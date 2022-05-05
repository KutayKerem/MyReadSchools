package com.kutaykerem.myreadschools;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kutaykerem.myreadschools.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ArtAdaptor extends RecyclerView.Adapter<ArtAdaptor.artholder> {





    ArrayList<Art> artArrayList;

    public ArtAdaptor(ArrayList<Art> artArrayList) {
        this.artArrayList = artArrayList;
    }


    @NonNull
    @Override
    public artholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new artholder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull artholder holder, int position) {
        holder.binding.recyclerViewTextView.setText(artArrayList.get(position).name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(),DetailsActivity.class);
                intent.putExtra("artId",artArrayList.get(holder.getAdapterPosition()).id);
                intent.putExtra("info","old");
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }
    public class artholder extends RecyclerView.ViewHolder {
        private RecyclerRowBinding binding;

        public artholder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}






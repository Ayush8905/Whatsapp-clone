package com.example.whatsappclone;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Item> itemList;
    private boolean isFromChats;

    // Constructor to accept the item list and the source flag
    public RecyclerViewAdapter(List<Item> itemList, boolean isFromChats) {
        this.itemList = itemList;
        this.isFromChats = isFromChats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutforrecycleview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.imageView.setImageResource(item.getImageResId());
        holder.label.setText(item.getLabel());
        holder.name.setText(item.getName());

        holder.l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isFromChats) {
                    intent = new Intent(holder.itemView.getContext(), activityforworkingchats.class);
                } else {
                    intent = new Intent(holder.itemView.getContext(), activityforstatusshow.class);

                }
                intent.putExtra("data_key", item.getLabel());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView name;
        ImageView imageView;
        LinearLayout l;

        ViewHolder(View itemView) {
            super(itemView);
            // label id is used for name
            // name id is used for number
            label = itemView.findViewById(R.id.label);
            name = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.imageView);
            l = itemView.findViewById(R.id.layoutid);
        }
    }
}

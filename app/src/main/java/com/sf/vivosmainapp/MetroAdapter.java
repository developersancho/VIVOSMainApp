package com.sf.vivosmainapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mesutgenc on 21.03.2018.
 */

public class MetroAdapter extends RecyclerView.Adapter<MetroAdapter.MetroViewHolder> {

    private Context mContext;
    private List<Metro> metroList;

    public MetroAdapter(Context mContext, List<Metro> metroList) {
        this.mContext = mContext;
        this.metroList = metroList;
    }

    @Override
    public MetroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        return new MetroViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MetroViewHolder holder, int position) {
        Metro metro = metroList.get(position);
        holder.textViewName.setText(metro.getName());
        holder.textViewAddress.setText(metro.getAddress());
    }

    @Override
    public int getItemCount() {
        return metroList.size();
    }

    public class MetroViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewAddress;

        public MetroViewHolder(View view) {
            super(view);
            textViewName = (TextView) view.findViewById(R.id.textViewName);
            textViewAddress = (TextView) view.findViewById(R.id.textViewAddress);
        }
    }
}

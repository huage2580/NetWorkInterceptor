package com.hua.netfloatview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class SpyAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<NetSpyPack> packList = new LinkedList<>();
    private OnItemClick onItemClick;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setPackList(List<NetSpyPack> packList) {
        this.packList = packList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network_list,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NetSpyPack pack = packList.get(position);
        holder.status.setEnabled(pack.getCode().equals("200"));
        holder.method.setText(pack.getMethod().toUpperCase());
        String[] segs = pack.getUrl().split("/");
        holder.title.setText(segs[segs.length-1]);
        holder.cost.setText((pack.getEndTime()-pack.getStartTime())+"ms");
        holder.url.setText(pack.getUrl());
        holder.itemView.setOnClickListener(v-> {
            if (onItemClick != null){
                onItemClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packList.size();
    }

    interface OnItemClick{
        void onClick(int index);
    }
}

class ViewHolder extends RecyclerView.ViewHolder{
    View status;
    TextView title;
    TextView method;
    TextView cost;
    TextView url;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        status = itemView.findViewById(R.id.status);
        title = itemView.findViewById(R.id.title);
        method = itemView.findViewById(R.id.method);
        cost = itemView.findViewById(R.id.cost);
        url = itemView.findViewById(R.id.url);
    }
}

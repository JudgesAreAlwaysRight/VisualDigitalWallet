package com.example.visualwallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualwallet.R;

import java.util.List;

public class showinfo_adapter extends RecyclerView.Adapter<showinfo_adapter.infoViewHolder> {
    private Context infoContext;

//    private List<String> list;
//    public showinfo_adapter(Context context) {
//        this.infoContext = context;
//    }
    public showinfo_adapter(Context context) {
        this.infoContext = context;
    }

    @NonNull
    @Override
    public showinfo_adapter.infoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new infoViewHolder(LayoutInflater.from(infoContext).inflate(R.layout.showinfo_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull showinfo_adapter.infoViewHolder holder, int position) {
        // TODO：在这里每次读取图片后设置信息，或许也可换个位置
        holder.txt.setText("SumingNB");
    }

    @Override
    public int getItemCount() {
//        return list.size();
        return 10;
    }

    class infoViewHolder extends RecyclerView.ViewHolder{
        private TextView txt;

        public infoViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.textView2);
        }
    }
}

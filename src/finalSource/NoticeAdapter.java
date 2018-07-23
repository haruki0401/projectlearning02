package com.example.tomoko.pro2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    private String[] nDataset = new String[20];

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dataTextView;

        public ViewHolder(View v) {
            super(v);
            dataTextView = v.findViewById(R.id.notice_list_view);
        }
    }

    public NoticeAdapter(String[] nDataset) {
        this.nDataset = nDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_text_view, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dataTextView.setText(nDataset[position]);
    }

    @Override
    public int getItemCount() {
        return nDataset.length;
    }
}

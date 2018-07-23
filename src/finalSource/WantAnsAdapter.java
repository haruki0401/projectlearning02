package com.example.tomoko.pro2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WantAnsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutID;
    private ArrayList<String> questionlist;//投稿した質問
    private ArrayList<String> numlist;

    static class ViewHolder {
        TextView text;
        TextView num;
    }

    WantAnsAdapter(Context context, int itemLayoutId,
                   ArrayList<String> questions, ArrayList<String> num) {
        super();
        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutID = itemLayoutId;
        questionlist = questions;
        numlist = num;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WantAnsAdapter.ViewHolder holder;
        //最初だけViewをinflateしてそれを再利用
        if(convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);
            holder = new WantAnsAdapter.ViewHolder();
            holder.num = convertView.findViewById(R.id.number_who_wanna_ask);
            holder.text = convertView.findViewById(R.id.wanna_ask_question);
            convertView.setTag(holder);
        } else {
            holder = (WantAnsAdapter.ViewHolder)convertView.getTag();
        }

        holder.num.setText(numlist.get(position));

        holder.text.setText(questionlist.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return questionlist.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
}

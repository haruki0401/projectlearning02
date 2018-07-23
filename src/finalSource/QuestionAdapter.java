package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutID;
    private ArrayList<String> questionlist;
    private ArrayList<Integer> imagelist;

    static class ViewHolder {
        TextView text;
        ImageView img;
    }




    QuestionAdapter(Context context, int itemLayoutId,
                    ArrayList questions, ArrayList images) {
        super();
        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutID = itemLayoutId;
        questionlist = questions;
        imagelist = images;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //最初だけViewをinflateしてそれを再利用
        if(convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);
            holder = new ViewHolder();
            holder.img = convertView.findViewById(R.id.question_icon);
            holder.text = convertView.findViewById(R.id.question_list_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.img.setImageResource(imagelist.get(position));

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

package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AnswerListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private ArrayList<String> questionerL;
    private ArrayList<String> questionL;


    static class ViewHolder {
        TextView questionerText;
        TextView questionText;
    }




    AnswerListAdapter(Context context, int itemLayoutId,
                      ArrayList<String> questioner, ArrayList<String> question) {
        super();
        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutID = itemLayoutId;
        questionerL = questioner;
        questionL = question;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //最初だけViewをinflateしてそれを再利用
        if(convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);
            holder = new ViewHolder();

            holder.questionerText = convertView.findViewById(R.id.ad_questioner_text);
            holder.questionText = convertView.findViewById(R.id.ad_question_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.questionerText.setText(questionerL.get(position));
        holder.questionText.setText(questionL.get(position));


        return convertView;
    }

    @Override
    public int getCount() {
        return questionerL.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
}






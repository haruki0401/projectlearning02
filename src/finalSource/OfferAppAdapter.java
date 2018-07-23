package com.example.tomoko.pro2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class OfferAppAdapter  extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private ArrayList<String> candidateNameL;
    private ArrayList<String> candidateAansL;
    private ArrayList<String> candidateQueL;
    private ArrayList<String> candidateEquL;
    private ArrayList<String> candidateJobL;
    private ArrayList<String> candidateBelongL;

    static class ViewHolder {
        TextView candidateName;
        TextView candidateAns;
        TextView candidateQue;
        TextView candidateEqu;
        TextView candidateJob;
        TextView candidateBelong;
    }




    OfferAppAdapter(Context context, int itemLayoutId,
                    ArrayList candidateN, ArrayList candidateA, ArrayList candidateQ, ArrayList candidateE,
                    ArrayList candidateJ, ArrayList candidateB) {
        super();

        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        candidateNameL = candidateN;
        candidateAansL = candidateA;
        candidateQueL = candidateQ;
        candidateEquL = candidateE;
        candidateJobL = candidateJ;
        candidateBelongL = candidateB;



        layoutID = itemLayoutId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OfferAppAdapter.ViewHolder holder;
        //最初だけViewをinflateしてそれを再利用
        if(convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);
            holder = new OfferAppAdapter.ViewHolder();
            holder.candidateName = convertView.findViewById(R.id.candidated_person_text_view);
            holder.candidateAns = convertView.findViewById(R.id.candidated_answer_num);
            holder.candidateQue = convertView.findViewById(R.id.candidated_question_num);
            holder.candidateEqu = convertView.findViewById(R.id.candidated_value_num);
            holder.candidateJob = convertView.findViewById(R.id.candidated_person_job);
            holder.candidateBelong = convertView.findViewById(R.id.candidated_person_belong);


            convertView.setTag(holder);
        } else {
            holder = (OfferAppAdapter.ViewHolder)convertView.getTag();
        }
        holder.candidateName.setText(candidateNameL.get(position));
        holder.candidateAns.setText(candidateAansL.get(position));
        holder.candidateQue.setText(candidateQueL.get(position));
        holder.candidateEqu.setText(candidateEquL.get(position));
        String s1 = "職業:   " + candidateJobL.get(position);
        String s2 = "所属:   " + candidateBelongL.get(position);
        holder.candidateJob.setText(s1);
        holder.candidateBelong.setText(s2);


        return convertView;
    }

    @Override
    public int getCount() {
        return candidateNameL.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
}

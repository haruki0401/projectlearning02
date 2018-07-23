package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ApplicantAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private String[] applicantlist;
    private String[] anumlist;
    private String[] qnumlist;
    private String[] eqnumlist;


    static class ViewHolder {
        TextView text;
        TextView qnum;
        TextView anum;
        TextView eqnum;
    }





    ApplicantAdapter(Context context, int itemLayoutId,
                   String[] applicants, String[] qnum, String[] anum, String[] eqnum) {
        super();
        this.inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutID = itemLayoutId;
        applicantlist = applicants;
        anumlist = anum;
        qnumlist = qnum;
        eqnumlist = eqnum;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ApplicantAdapter.ViewHolder holder;
        //最初だけViewをinflateしてそれを再利用
        if(convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);
            holder = new ApplicantAdapter.ViewHolder();

            //holder.anum = convertView.findViewById(R.id.app_q_num);
            //holder.qnum = convertView.findViewById(R.id.app_a_num);
            //holder.eqnum = convertView.findViewById(R.id.app_e_num);
            //holder.text = convertView.findViewById(R.id.applicant_name);
            convertView.setTag(holder);
        } else {
            holder = (ApplicantAdapter.ViewHolder)convertView.getTag();
        }

        holder.qnum.setText(qnumlist[position]);
        holder.anum.setText(anumlist[position]);
        holder.eqnum.setText(eqnumlist[position]);

        holder.text.setText(applicantlist[position]);

        return convertView;
    }

    @Override
    public int getCount() {
        return applicantlist.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
}



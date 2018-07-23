package com.example.tomoko.pro2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ApplicantActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener {
    String[] aDataset = {
            "回答希望者１", "回答希望者２","回答希望者3", "回答希望者4", "回答希望者5"
    };

    String[] qnumber = {
            "5", "2", "3", "2", "1"
    };

    String[] anumber = {
            "2", "6", "4", "3,", "2"
    };

    String[] eqnumber = {
            "1", "3", "1", "4", "3"
    };

    private DialogFragment dialogFragment;
    private FragmentManager flagmentManager;
    String chosenName;

    User user;
    Client client;
    Activity activity;

    static Client c;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        c = client;

        ListView anslistView = (ListView)findViewById(R.id.question_room_list_view);

        //BaseAdapterを継承したadapterのインスタンスを生成
        BaseAdapter adapter = new ApplicantAdapter(this.getApplicationContext(), R.layout.applicant_text_view,
                aDataset, qnumber, anumber, eqnumber);

        anslistView.setAdapter(adapter);
        anslistView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


            Intent intent = new Intent(this.getApplicationContext(), CheckQuestionActivity.class);
            String selectedText = aDataset[position];
            chosenName = aDataset[position];
            intent.putExtra("Text", selectedText);
            startActivity(intent);

            flagmentManager = getSupportFragmentManager();

            dialogFragment = new CheckQuestionActivity.AlertDialogFragment();
            dialogFragment.show(flagmentManager, "help_alert_dialogue");

    }

    public static class AlertDialogFragment extends DialogFragment {
        //選択肢のリスト
        private String[] menulist = {"オファーする", "オファーしない"};

        @Override
        @NonNull


        public Dialog onCreateDialog(Bundle saveInstanceState) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            //タイトル
            alert.setTitle("オファーを申し込みますか");
            alert.setItems(menulist, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //申し込む
                    if (i == 0) {
                        c.sendOffer("questoin","answer");
                    }
                    //申し込まない
                    else {
                        //
                    }
                }
            });
            return alert.create();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        client.logoutRequest();
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
        return true;
    }
}

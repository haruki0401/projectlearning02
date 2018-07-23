package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import java.util.ArrayList;

public class RegistUserActivity extends AppCompatActivity {

    private TextView userNameView;
    private EditText feature;
    private Button registButton;
    private ImageButton searchButton;
    private ListView groupListView;
    private EditText searchGroupText;


    private String userName;
    private String spinnerItems[] = {"高校生", "大学生", "大学院生", "公務員",
            "教員", "会社員", "自営業", "主婦", "そのほか"};//内容未定
    private String item;
    private String form_group_textView;

    private ArrayList<String> grouplist;

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_user);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        grouplist = new ArrayList<>();

        userName = user.getName();
        System.out.println("userName");
        userNameView = findViewById(R.id.userNameView2);
        userNameView.setText(userName);
        feature = (EditText)findViewById(R.id.feature_text);
        Spinner spinner = findViewById(R.id.professionspinner);
        searchButton = findViewById(R.id.search_button);
        groupListView = (ListView)findViewById(R.id.group);
        searchGroupText = findViewById(R.id.group_search_name);
        int n = 0;

        String job = user.getJob();

        if (job.equals("高校生")) {
            n = 0;
        } else if(job.equals("大学生")) {
            n = 1;
        } else if(job.equals("大学院生")) {
            n = 2;
        } else if(job.equals("公務員")) {
            n = 3;
        } else if(job.equals("教員")) {
            n = 4;
        } else if(job.equals("会社員")) {
            n = 5;
        } else if(job.equals("自営業")) {
            n = 6;
        } else if(job.equals("主婦")) {
            n = 7;
        } else if(job.equals("そのほか")) {
            n = 8;
        } else {
            n = 0;
        }

        //userNameView.setText(userName);
        feature.setText(user.getBelong());


        grouplist = user.getGroup();

        ArrayList<String> items = new ArrayList<>();

        for(int i = 0; i < grouplist.size(); i++) {
            items.add(grouplist.get(i));
        }

        final ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                this,
                R.layout.group_text,
                user.getGroup()
        );

        groupListView.setAdapter(groupAdapter);
        //group.setOnItemClickListener(this);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(n);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner = (Spinner)adapterView;
                item = (String)spinner.getSelectedItem();
                user.setBelong(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        registButton = findViewById(R.id.registSetting);
        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String job = item;
                String belong = feature.getText().toString();
                client.setListener(createListener());
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("設定完了")) {
                            toastMake(result, 0, -200);
                            client.receiveUserInformation(userName);
                        }
                        toastMake(result,0,-200);
                        return false;
                    }
                });
                //ユーザ情報を登録する
                client.sendUserInformation(job, belong, grouplist);

                //user.setBelong(feature.getText().toString());
                //user.setGroup(grouplist);


            }

        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String s = searchGroupText.getText().toString();

                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("グループが見つかりました")) {
                            toastMake(result, 0, -200);
                            user.getGroup().add(s);
                            groupListView.setAdapter(groupAdapter);//あとで
                            ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(
                                    activity,
                                    R.layout.group_text,
                                    user.getGroup()
                            );

                            groupListView.setAdapter(groupAdapter);

                        } else if(result.equals("グループは見つかりませんでした")) {
                            toastMake(result, 0, -200);
                        } else {
                            toastMake(result, 0, -200);

                        }
                        return false;
                    }
                });
                client.requestGroupSearch(s);
                //Intent intent = new Intent(getApplication(), GroupSearchActivity.class);
                //intent.putExtra("Keyword", searchGroupText.getText().toString());
                //user.setBelong(feature.getText().toString());//あとで
                //startActivity(intent);
            }
        });


    }
    void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
    }

    private Client.Listener createListener() {

        return new Client.Listener() {

            @Override
            public void onSuccess(int f) {

                if(f == 0) {//うまくいくかわからない
                    //toastMake("情報取得",0,-200);
                    Intent intent = new Intent(activity ,HomeActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

}

package com.example.tomoko.pro2;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.tomoko.pro2.R.layout.group_text_view;

public class HomeActivity extends AppCompatActivity {
    TextView userNameView;

    Button editButton;
    ImageButton homeButton;
    ImageButton talkButton;
    ImageButton noticeButton;
    ImageButton moneyButton;
    ImageButton groupButton;

    TextView userProfession;
    TextView userFeature;
    ListView userGroupListView;
    TextView remainCoin;
    TextView questionN;
    TextView answerN;
    TextView evaluN;

    List<String> groups;

    User user;
    Client client;
    Activity activity;

    String[] groupn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        String name = user.getName();




        userNameView = findViewById(R.id.user_name_view3);

        userNameView.setText(name);

        editButton = findViewById(R.id.edit_button);
        homeButton = findViewById(R.id.home_button);
        noticeButton = findViewById(R.id.notice_button);
        moneyButton = findViewById(R.id.money_button);
        talkButton = findViewById(R.id.talk_button);
        groupButton = findViewById(R.id.group_button);
        remainCoin = findViewById(R.id.remindmoney_num_view);
        questionN = (TextView)findViewById(R.id.question_n_text) ;
        answerN = (TextView)findViewById(R.id.answer_num_text);
        evaluN = (TextView)findViewById(R.id.rate_text) ;

        userProfession = (TextView)findViewById(R.id.profession_text_view);
        userFeature = (TextView)findViewById(R.id.feature_text_view);
        userGroupListView = (ListView)findViewById(R.id.group_list_view);
        userProfession.setText(user.getJob());
        userFeature.setText(user.getBelong());
        remainCoin.setText(String.valueOf(user.getCoin()));
        questionN.setText(String.valueOf(user.getQuestion()));
        answerN.setText(String.valueOf(user.getAnswer()));
        evaluN.setText(String.valueOf(user.getValue()));


        /*groupn = new String[user.getGroup().size()];
        for(int i = 0; i < groupn.length; i++) {
            groupn[i] = user.getGroup().get(i);
        }*/
        //groups.add("kon");
        groups = user.getGroup();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                group_text_view,//ここわからなくなってしまった
                groups
        );

        userGroupListView.setAdapter(adapter);


        //ホームボタンを無効化する
        //homeButton.setEnabled(false);

        //編集ボタンで設定の変更、所属等を設定する画面へ遷移
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), RegistUserActivity.class);

                startActivity(intent);
            }
        });

        //グループボタンが押されたらグループ画面へ
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), GroupActivity.class);
                startActivity(intent);
            }

        });

        //質問管理ボタンが押されたら質問管理画面へ
        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TalkManageActivity.class);
                startActivity(intent);
            }
        });

        //お知らせボタンが押されたらお知らせ画面へ
        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), NoticeActivity.class);
                startActivity(intent);
            }
        });

        //お金管理が押されたら、お金管理画面へ
        moneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MoneyActivity.class);
                startActivity(intent);
            }
        });
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

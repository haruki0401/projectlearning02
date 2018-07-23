package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;


public class NoticeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ImageButton homeButton;
    ImageButton talkButton;
    ImageButton moneyButton;
    ImageButton groupButton;

    private String[] noticeText = {
            "お知らせ1", "お知らせ2", "お知らせ3", "お知らせ4", "お知らせ5", "お知らせ6"
    };

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        ListView noticeListView = (ListView)findViewById(R.id.notice_list_view);

        ArrayList<String> items = new ArrayList<>();

        for(int i = 0; i < noticeText.length; i++) {
            items.add(noticeText[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.notice_text_view,
                items
        );

        noticeListView.setAdapter(adapter);
        noticeListView.setOnItemClickListener(this);


        homeButton = findViewById(R.id.home_button);
        moneyButton = findViewById(R.id.money_button);
        talkButton = findViewById(R.id.talk_button);
        groupButton = findViewById(R.id.group_button);

        /*noticeRecyclerView = findViewById(R.id.notice_recycler_view);

        noticeRecyclerView.setHasFixedSize(true);

        noticeLayoutManager = new LinearLayoutManager(this);

        noticeRecyclerView.setLayoutManager(noticeLayoutManager);

        for(int i = 0; i < noticeDataset.length; i++) {
            noticeDataset[i] = "Data_0" + String.valueOf(i);
        }

        noticeAdapter = new NoticeAdapter(noticeDataset);
        noticeRecyclerView.setAdapter(noticeAdapter);*/
        //ホームボタンが押されたらホーム画面へ
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setListener(createListener());
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("取得成功")) {
                            Intent intent = new Intent(getApplication(), HomeActivity.class);
                            startActivity(intent);
                        } else {
                        }
                        return false;
                    }
                });
                client.receiveUserInformation(user.getName());

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

        //お金管理が押されたら、お金管理画面へ
        moneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MoneyActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(this.getApplicationContext(), NoticeMoreActivity.class);
        //選択された場所を取得
        String selectedText = noticeText[position];
        //遷移先のActivityに情報を提供
        intent.putExtra("Text", selectedText);
        startActivity(intent);
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

    private Client.Listener createListener() {

        return new Client.Listener() {

            @Override
            public void onSuccess(int f) {
                //情報取得成功ならば
                if(f == 0) {
                    Intent intent = new Intent(getApplication(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        };
    }
}

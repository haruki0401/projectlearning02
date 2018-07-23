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

public class GroupActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ImageButton homeButton;
    ImageButton talkButton;
    ImageButton noticeButton;
    ImageButton moneyButton;
    ImageButton addButton;
    ListView groupListView;

    private String[] gDataset = {
            "情報工学", "横国", "理工系", "文系", "数学", "英語", "本", "物理"
    };

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();


        groupListView =(ListView)findViewById(R.id.group_list_view);

        /*ArrayList<String> items = new ArrayList<>();
        ArrayList<String> grouplist = user.getGroup();
        for(int i = 0; i < grouplist.size(); i++) {
            items.add(grouplist.get(i));
        }*/





        ArrayList<String> g = user.getGroup();

        /*for(int i = 0; i < g.size(); i++) {
            client.setOnCallBack(new Client.CallBackTask() {
                public void CallBack(String result) {
                    super.CallBack(result);
                    if(result.equals("取得成功")) {
                    } else {
                    }
                }
            });
            client.receiveQuestions(g.get(i),activity);
        }*/

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this,
            R.layout.group_text_view,
            g
        );

        groupListView.setAdapter(adapter);
        groupListView.setOnItemClickListener(this);

        homeButton = findViewById(R.id.home_button);
        noticeButton = findViewById(R.id.notice_button);
        moneyButton = findViewById(R.id.money_button);
        talkButton = findViewById(R.id.talk_button);
        addButton = findViewById(R.id.add_group_button2);



        //ホームボタンが押されたらホーム画面へ
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setListener(createListener());
                client.receiveUserInformation(user.getName());

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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), AddGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String selectedText = user.getGroup().get(position);

        client.setOnCallBack(new Client.CallBackTask() {
            public boolean CallBack(String result) {
                super.CallBack(result);
                if(result.equals("取得成功")) {

                    Intent intent = new Intent(getApplication(), QuestionRoomActivity.class);

                    startActivity(intent);
                } else {
                    //toastMake(result, 0, -200);
                }
                return false;
            }
        });

        client.receiveGroupInformation(selectedText);//グループ内での質問を受け取る

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

    private Client.Listener createListener() {//不安がある

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

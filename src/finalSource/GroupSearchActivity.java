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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;

public class GroupSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String keyword;
    String[] groupData = {
            "横浜国立大学", "理工系", "文系", "音楽", "音楽大学", "情報工学", "数物電子情報系", "数学",
            "横浜市立大学", "横浜スタジアム", "横浜", "理学部", "工学部", "パソコン", "大学", "国立大学"
    };
    String[] keywords;
    List<String> result;


    TextView searchWord;
    ListView noticeListView;

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_group_search);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        //searchWord = findViewById(R.id.searched_keyword);

        Intent intent = getIntent();
        keyword = intent.getStringExtra("Keyword");

        searchWord.setText(keyword);

        result = new ArrayList<>();
        //サーバ側が実装するのかな？一応
        keywords = keyword.split(" ");

        for(int i = 0; i < keywords.length; i++) {
            for(int j = 0; j< groupData.length; j++) {
                if(groupData[j].contains(keywords[i])) {
                    result.add(groupData[j]);
                }
            }
        }

        //noticeListView = (ListView)findViewById(R.id.group_serach_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.group_search_text_view,
                R.id.group_name_result,
                result
        );


        noticeListView.setAdapter(adapter);
        noticeListView.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(getApplication(), RegistUserActivity.class);
        //参加グループを追加
        user.getGroup().add(result.get(position));
        //遷移先のActivityに情報を提供
        //finish();
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
}

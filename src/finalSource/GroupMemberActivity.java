package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

//メンバーのリストを出力させる
public class GroupMemberActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ListView groupList;
    private ActionBarDrawerToggle groupToggle;
    private CharSequence gDrawerTitle;
    private CharSequence gTitle;

    private String groupName;

    private ArrayList<User> members;

    //メンバーの名前
    private String[] memberName = {
            "佐藤","鈴木","高橋", "田中", "伊藤", "渡辺", "山本", "中村", "小林"
    };

    User user;
    Client client;
    Activity activity;

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.activity_group_member);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        members = client.getMyGroup().getmember();
        groupName = client.getMyGroup().getgname();

        groupList = (ListView)findViewById(R.id.group_member_list_view);

        ArrayList<String> items = new ArrayList<>();

        for(int i = 0; i < members.size(); i++) {
            String s = members.get(i).getName() + "\t: " + members.get(i).getBelong();
            items.add(s);//名前を追加
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.group_member_text_view,
                items
        );

        groupList.setAdapter(adapter);
        groupList.setOnItemClickListener(this);


    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        //選択された場所を取得
        Intent intent = new Intent(activity, AskQuestionActivity.class);
        intent.putExtra("teacher", 0);//0:先生を指定している
        intent.putExtra("Text", members.get(position).getName());//選択された先生の名前
        intent.putExtra("group", groupName);
        startActivity(intent);



        //遷移先のActivityに情報を提供


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



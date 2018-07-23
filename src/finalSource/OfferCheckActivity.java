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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//自分へきたオファーの確認
public class OfferCheckActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String[] questionerDataset = {
        "質問者1", "質問者2", "質問者3", "質問者4", "質問者5"
    };

    User user;
    Client client;
    Activity activity;

    ArrayList<String> questionerName;
    ArrayList<String> questionS;
    ArrayList<Question> questions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_check);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        questionerName = new ArrayList<>();
        questionS = new ArrayList<>();
        ListView offerListView = (ListView)findViewById(R.id.offer_check_list_view);
        questions = client.getMyOffer();
        //questions = client.ge
        for(int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            questionerName.add(q.getQuestioner().getName());
            questionS.add(q.getQuestion());
        }

        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for(int i = 0; i < questionerName.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("main", questionerName.get(i));
            map.put("sub", questionS.get(i));
            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2,
                new String[] {"main", "sub"},
                new int[] {android.R.id.text1, android.R.id.text1}
        );

        offerListView.setAdapter(adapter);
        offerListView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        client.setWatchingQ(questions.get(position));
        Intent intent = new Intent(this.getApplicationContext(), ReturnAnswerActivity.class);
        //選択された場所を取得
        String selectedText = questionS.get(position);//質問を取得
        client.setWatchingQ(questions.get(position));

        //遷移先のActivityに情報を提供
        intent.putExtra("Text", selectedText);//質問内容
        intent.putExtra("Questioner", questionerName.get(position));//質問者
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

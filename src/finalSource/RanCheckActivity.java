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

public class RanCheckActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ImageButton homeButton;
    private String[] ranDataset = {
            "お知らせ1", "お知らせ2", "お知らせ3", "お知らせ4", "お知らせ5", "お知らせ6"
    };

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ran_check);

        ListView ranListView = (ListView)findViewById(R.id.ran_check_list_view);

        ArrayList<String> items = new ArrayList<>();

        for(int i = 0; i < ranDataset.length; i++) {
            items.add(ranDataset[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.ran_check_text_view,
                items
        );

        ranListView.setAdapter(adapter);
        ranListView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //Intent intent = new Intent(this.getApplicationContext(), NoticeMoreActivity.class);
        //選択された場所を取得
        //String selectedText = ranDataset[position];
        //遷移先のActivityに情報を提供
        //intent.putExtra("Text", selectedText);
        //startActivity(intent);
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

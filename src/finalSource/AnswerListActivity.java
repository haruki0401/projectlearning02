package com.example.tomoko.pro2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AnswerListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //自分が立候補した質問を見る

    String[] askedDataset = {
            "わからないことがあります", "ずっと気になっていたのですが","明日までに解かなければならない問題があります"
            , "教科書のxページが難しいです", "回答まっています"
    };
    //回答あり:1、回答待ち:0
    int[] qStateDataset = {
            1,0,1,1,0
    };

    String[] teacherDataset = {
            "回答者1", "回答者2", "回答者3", "回答者4", "回答者5"
    };

    User user;
    Client client;
    Activity activity;

    ArrayList<Question> questions;
    ArrayList<String> question;//質問内容
    ArrayList<String> questioner;//質問者

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        ListView anslistView = (ListView)findViewById(R.id.answer_list_view);

        questions = client.getMyCandidacy();//質問のArrayListを取得

        question = new ArrayList<>();
        questioner = new ArrayList<>();

        for(int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            question.add(q.getQuestion());
            questioner.add(q.getQuestioner().getName());
        }

        //BaseAdapterを継承したadapterのインスタンスを生成
        BaseAdapter adapter = new AnswerListAdapter(this.getApplicationContext(), R.layout.answer_text_view,
                questioner, question);

        anslistView.setAdapter(adapter);
        anslistView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //自分が立候補した質問内容の確認へ
            client.setWatchingQ(questions.get(position));//見ている質問に登録
            Intent intent = new Intent(this.getApplicationContext(), CheckCandActivity.class);

            intent.putExtra("Text", question.get(position));
            intent.putExtra("Questioner", questioner.get(position));
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

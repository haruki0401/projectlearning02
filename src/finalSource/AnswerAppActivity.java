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
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
//自分がした質問の管理を行う
public class AnswerAppActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String[] qDataset = {
            "質問があります,0", "悩んでいます,1","どうやって解けばよいですか,0"
    };

    String[] number = {
            "5", "2", "3"
    };

    String[] questions;
    String[] numbers;

    User user;
    Client client;
    Activity activity;
    ArrayList<String> question;
    ArrayList<String> questionState;//質問の状態を確認
    ArrayList<Question> myquestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_who_wanna_ans);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        question = new ArrayList<>();
        questionState = new ArrayList<>();
        myquestion = client.getMyQuestion();

        int n;

        for(int i = 0; i < myquestion.size(); i++) {
            Question q = myquestion.get(i);
            question.add(q.getQuestion());
            if(q.getOffer() != null) {//すでにオファーしているならば
                if (q.checkAnswered()) {//回答がすでにあるならば
                    questionState.add(q.getAnswerer().getName() + "からの回答があります");
                } else {//回答を待っているならば
                    questionState.add(q.getOffer().getName() + "からの回答待ちです");
                }
            } else {//立候補者を募っているならば
                questionState.add(String.valueOf(q.getCandidates().size()) + "人の立候補者がいます");
            }
        }



        ListView anslistView = (ListView)findViewById(R.id.who_wanna_ask_list_view);

        //BaseAdapterを継承したadapterのインスタンスを生成
        BaseAdapter adapter = new WantAnsAdapter(this.getApplicationContext(), R.layout.who_wanna_ask_text_view,
                question, questionState);

        anslistView.setAdapter(adapter);
        anslistView.setOnItemClickListener(this);

    }
    //選択された質問について、候補者を表示
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String state = questionState.get(position);//クリックされた質問の状態を確認
        String selectedText = question.get(position);
        client.setWatchingQ(myquestion.get(position));
        if(state.contains("からの回答があります")) {
            //回答評価画面へ
            Intent intent = new Intent(this.getApplicationContext(), AnswerEquivalentActivity.class);
            /*intent.putExtra("Text", selectedText);//質問内容を取得
            intent.putExtra("Answer", myquestion.get(position).getAnswer());//回答を取得
            intent.putExtra("Teacher", myquestion.get(position).getAnswerer().getName());//回答者を取得*/
            startActivity(intent);
        } else if(state.contains("からの回答待ちです")) {
            //質問を取り下げるかどうか
            Intent intent = new Intent(this.getApplicationContext(), AnswerCheckActivity.class);
            /*intent.putExtra("Text", selectedText);
            intent.putExtra("Teacher", myquestion.get(position).getAnswerer().getName());//回答してほしい人の名前を取得*/
            startActivity(intent);
            //質問内容の確認、かつ取り下げるかどうか
        } else if(state.contains("人の立候補者がいます")) {
            //オファーのメンバーを確認
            Intent intent = new Intent(this.getApplicationContext(), OfferAppActivity.class);
            /*intent.putExtra("Position", position);
            intent.putExtra("Question", myquestion.get(position).getQuestion());*/
            startActivity(intent);

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
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class QuestionRoomActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //解決済み:1, 未解決:0

    int[] images = {
            R.drawable.ic_sentiment_very_dissatisfied_black_24dp,
            R.drawable.ic_sentiment_very_satisfied_black_24dp
    };

    ArrayList<String> questions;
    ArrayList<Question> questionsQ;
    ArrayList<Integer> states;
    String[] str;

    Button askButton;
    ImageButton memberButton;
    TextView groupExpl;

    User user;
    Client client;
    Activity activity;

    Group g;

    static final int RESULT_SUBACTIVITY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_room);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Intent intent = getIntent();
        //String gName = intent.getStringExtra("Text");

        g = client.getMyGroup();

        setTitle(g.getgname());

        groupExpl = (TextView)findViewById(R.id.group_ex);
        //final ListView qlistView = (ListView)findViewById(R.id.question_room_list_view);


        groupExpl.setText(g.getintro());

        /*questionsQ = g.getchat();
        //System.out.println("質問読み込む");
        for(int i = 0; i < questionsQ.size(); i++) {
            Question q = questionsQ.get(i);
            String s = q.getQuestion();
            questions.add(s);//あとで直す
            //questions.add("こまった");
            if(q.checkAnswered()) {
                states.add(images[1]);
            } else {
                states.add(images[0]);
            }
        }
        BaseAdapter adapter = new QuestionAdapter(activity.getApplicationContext(), R.layout.question_text_view,
                questions, states);*/

        //qDatasetから質問と質問の状態抽出


        //qlistView.setAdapter(adapter);
        
        //questions = new String[qDataset.length];
        //states = new int[qDataset.length];

        askButton = (Button)findViewById(R.id.ask_button);
        memberButton = (ImageButton)findViewById(R.id.member_button);

        /*for(int i = 0; i < qDataset.length; i++) {
            str = qDataset[i].split(",");
            questions[i] = str[0];
            states[i] = images[parseInt(str[1])];
        }*/


        //qlistView.setOnItemClickListener(this);
        //BaseAdapterを継承したadapterのインスタンスを生成
        //BaseAdapter adapter = new QuestionAdapter(this.getApplicationContext(), R.layout.question_text_view,
                //questions, states);



        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), AskQuestionActivity.class);
                //intent.putExtra("Activity", )
                intent.putExtra("teacher", 1);//先生を指定していない
                intent.putExtra("group", client.getMyGroup().getgname());//グループ名を送信
                startActivity(intent);
            }
        });

        memberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("取得成功")) {
                            ;
                            Intent intent = new Intent(getApplication(), GroupMemberActivity.class);
                            startActivity(intent);

                            //Intent intent = new Intent(getApplication(), HomeActivity.class);
                            //startActivity(intent);
                        } else {

                        }
                        return false;
                    }
                });
                client.receiveGroupInformation(client.getMyGroup().getgname());

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


        final ListView qlistView = (ListView)findViewById(R.id.question_room_list_view);
        questions = new ArrayList<String>();
        states = new ArrayList<Integer>();
        questionsQ = g.getchat();
                    //System.out.println("質問読み込む");
        for(int i = 0; i < questionsQ.size(); i++) {
            Question q = questionsQ.get(i);
            String s = q.getQuestion();
            questions.add(s);//あとで直す
            //questions.add("こまった");
            if(q.checkAnswered()) {
                states.add(images[1]);
                } else {
                    states.add(images[0]);
                }
                }
                BaseAdapter adapter = new QuestionAdapter(activity.getApplicationContext(), R.layout.question_text_view,
                        questions, states);

                    //qDatasetから質問と質問の状態抽出


        qlistView.setAdapter(adapter);
        qlistView.setOnItemClickListener(this);



    }

    /*protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK && requestCode == RESULT_SUBACTIVITY &&
                null != intent) {
            final ListView qlistView = (ListView)findViewById(R.id.question_room_list_view);
            questionsQ = g.getchat();
            //System.out.println("質問読み込む");
            for(int i = 0; i < questionsQ.size(); i++) {
                Question q = questionsQ.get(i);
                String s = q.getQuestion();
                questions.add(s);//あとで直す
                //questions.add("こまった");
                if(q.checkAnswered()) {
                    states.add(images[1]);
                } else {
                    states.add(images[0]);
                }
            }
            BaseAdapter adapter = new QuestionAdapter(activity.getApplicationContext(), R.layout.question_text_view,
                    questions, states);

            qlistView.setAdapter(adapter);
            qlistView.setOnItemClickListener(this);
        }
    }*/

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        client.setWatchingQ(questionsQ.get(position));//注目中の質問として取っておく
        //未解決ならば
        if(states.get(position) == R.drawable.ic_sentiment_very_dissatisfied_black_24dp) {
            Intent intent = new Intent(this.getApplicationContext(), CheckQuestionActivity.class);
            String selectedText = questions.get(position);
            User u = questionsQ.get(position).getQuestioner();
            intent.putExtra("questioner", u.getName());
            intent.putExtra("questiondN", String.valueOf(u.getQuestion()));
            intent.putExtra("answerNum", String.valueOf(u.getAnswer()));
            intent.putExtra("valueN", String.valueOf(u.getValue()));
            intent.putExtra("Text", selectedText);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this.getApplicationContext(), WatchQuestionActivity.class);
            Question q = questionsQ.get(position);
            //選択された場所を取得
            String selectedText = questions.get(position);//質問を取得
            //遷移先のActivityに情報を提供
            intent.putExtra("Text", selectedText);//質問内容
            intent.putExtra("Teacher", q.getAnswerer().getName());//回答者
            intent.putExtra("Answer", q.getAnswer());//回答内容
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

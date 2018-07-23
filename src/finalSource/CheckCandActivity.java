package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CheckCandActivity extends AppCompatActivity {
//自分が立候補した質問の内容確認
    String questioner;
    String question;
    TextView questionnerTextView;
    TextView questionTextView;
    Button refuseButton;
    Button imageButton;

    User user;
    Client client;
    Activity activity;

    String isImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_cand);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Intent intent = getIntent();
        questioner = intent.getStringExtra("Questioner");//質問者
        question = intent.getStringExtra("Text");//質問内容
        isImage = intent.getStringExtra("Image");//画像のうむ

        questionnerTextView = (TextView)findViewById(R.id.questioner_check_text);
        questionTextView = (TextView)findViewById(R.id.question_check_text);
        refuseButton = (Button)findViewById(R.id.cancel_button);
        imageButton = (Button)findViewById(R.id.image_button3);//画像を出力

        Question q = client.getWatchingQ();
        String s = q.getQuestioner().getName() + "さんからの質問に立候補しています";
        questionnerTextView.setText(q.getQuestioner().getName());
        questionTextView.setText(q.getQuestion());

        refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("取り消しました")) {
                            toastMake("立候補を取り消しました", 0, -200);

                        } else {
                            toastMake("立候補の取り消しに失敗しました", 0, -200);
                        }
                        return false;
                    }
                });
                client.cancelCandidacy(question);

            }
        });

        if(q.getIsImage()) {//画像が存在するならば
            imageButton.setVisibility(View.VISIBLE);
        } else {//画像が存在しないならば
            imageButton.setVisibility(View.INVISIBLE);//見えない
        }
        //imageButton.setVisibility(View.INVISIBLE);//見えない
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ImageActivity.class);
                //intent.putExtra("Image", "question");
                startActivity(intent);
            }
        });


    }

    void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
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

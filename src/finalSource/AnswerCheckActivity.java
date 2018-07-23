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

public class AnswerCheckActivity extends AppCompatActivity {
    TextView teacherName;
    TextView askedQuestion;
    Button turnDownButton;
    String teacher;
    String question;
    Button imageButton;

    User user;
    Client client;
    Activity activity;

    String image;//画像の有無


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_check);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Question q = client.getWatchingQ();

        teacherName = (TextView)findViewById(R.id.asked_teacher_name_text);
        askedQuestion = (TextView)findViewById(R.id.asked_quesiton_text_view);
        turnDownButton = (Button)findViewById(R.id.turn_down_button);
        imageButton = (Button)findViewById(R.id.watch_image);

        Intent intent = getIntent();

        /*teacher = intent.getStringExtra("Teacher");
        question = intent.getStringExtra("Text");
        image = intent.getStringExtra("Image");//画像の名前・有無*/

        if(q.getIsImage()) {//画像が存在するならば
            imageButton.setVisibility(View.VISIBLE);
        } else {//画像が存在しないならば
            imageButton.setVisibility(View.INVISIBLE);//見えない
        }

        String s = q.getOffer().getName() + "さんからの質問待ち";
        //imageButton.setVisibility(View.INVISIBLE);//見えない
        teacherName.setText(s);
        askedQuestion.setText(q.getQuestion());

        //拒否ボタン
        turnDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);

                        toastMake(result, 0, -200);

                        return false;
                    }
                });
                client.cancelOffer(question);

            }
        });

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

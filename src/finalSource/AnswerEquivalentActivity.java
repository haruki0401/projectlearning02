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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

//回答を評価
public class AnswerEquivalentActivity extends AppCompatActivity {

    TextView teacherNameTextView;
    TextView askedQuestionTextView;
    TextView returnedAnswerTextView;
    RatingBar answerEqBar;
    Button equivButton;
    Button imageButton;
    Button imageButton2;

    String teacherName;
    String questionStr;
    String answerStr;
    String isImage;
    String isImage2;

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_equivalent);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Question q = client.getWatchingQ();
        Intent intent = getIntent();

        /*teacherName = intent.getStringExtra("Teacher") + "さんからの回答";
        questionStr = intent.getStringExtra("Text");
        answerStr = intent.getStringExtra("Answer");
        isImage = intent.getStringExtra("Image");*/

        questionStr = q.getQuestion();

        teacherNameTextView = findViewById(R.id.teacher_name_text);
        askedQuestionTextView = findViewById(R.id.my_question_text);
        returnedAnswerTextView = findViewById(R.id.returned_answer_text);
        answerEqBar = findViewById(R.id.answer_rating_bar);
        equivButton = findViewById(R.id.send_equiv_button);
        imageButton = (Button)findViewById(R.id.image_button2);//画像を出力
        imageButton2 = (Button)findViewById(R.id.image_button8);

        teacherNameTextView.setText(q.getAnswerer().getName());
        askedQuestionTextView.setText(questionStr);
        returnedAnswerTextView.setText(q.getAnswer());

        answerEqBar.setIsIndicator(false);

        if(q.getIsImage()) {//画像が存在するならば
            imageButton.setVisibility(View.VISIBLE);
        } else {//画像が存在しないならば
            imageButton.setVisibility(View.INVISIBLE);//見えない
        }
        //imageButton.setVisibility(View.INVISIBLE);//見えない

        /*isImage2 = intent.getStringExtra("Image2");

        if(!isImage.equals("noImage")) {//画像が存在するならば
            imageButton2.setVisibility(View.VISIBLE);
        } else {//画像が存在しないならば
            imageButton2.setVisibility(View.INVISIBLE);//見えない
        }*/
        imageButton2.setVisibility(View.INVISIBLE);//見えない


        equivButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //評価値を取得
                float rate = answerEqBar.getRating();
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);

                        toastMake(result, 0, -200);
                        return false;
                    }
                });
                client.sendValue(questionStr, rate);
            }
        });

        answerEqBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

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

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ImageActivity.class);
                intent.putExtra("Image", "anwer");
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

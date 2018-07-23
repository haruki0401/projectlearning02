package com.example.tomoko.pro2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class WatchQuestionActivity extends AppCompatActivity {
    String teacherName;
    String questionStr;
    String answerStr;
    TextView teacherTextView;//回答者の名前
    TextView questionTextView;
    TextView answerTextView;
    //RatingBar ratingBar;
    Button alertButton;
    //Button sendEqButton;


    private DialogFragment dialogFragment;
    private FragmentManager flagmentManager;

    User user;
    Client client;
    Activity activity;

    Button imageButton2;
    Button imageButton;
    String isImage;
    String isImage2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_question);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Question q = client.getWatchingQ();

        Intent intent = getIntent();

        questionTextView = findViewById(R.id.question_text);
        answerTextView = findViewById(R.id.answer_text);
        //ratingBar = findViewById(R.id.rating_bar_wq);
        alertButton = findViewById(R.id.alert_button);
        //sendEqButton = findViewById(R.id.send_equivalent_button);
        teacherTextView = (TextView)findViewById(R.id.teacher_name_text_view);
        imageButton2 = (Button)findViewById(R.id.image_button8);


        teacherName = q.getAnswerer().getName() + "さんの回答";//intent.getStringExtra("Teacher") + "さんの回答";
        questionStr = q.getQuestion(); //intent.getStringExtra("Text");
        answerStr = q.getAnswer(); //intent.getStringExtra("Answer");



        teacherTextView.setText(teacherName);
        questionTextView.setText(questionStr);
        answerTextView.setText(answerStr);

        //ratingBar.setIsIndicator(false);

        //isImage2 = intent.getStringExtra("Image2");

        /*if(!isImage.equals("noImage")) {//画像が存在するならば
            imageButton2.setVisibility(View.VISIBLE);
        } else {//画像が存在しないならば
            imageButton2.setVisibility(View.INVISIBLE);//見えない
        }*/
        //imageButton2.setVisibility(View.INVISIBLE);//見えない

        /*sendEqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //評価値を取得
                int rate = ratingBar.getNumStars();
                client.sendValue(questionStr, rate);//評価値を送信する

            }
        });*/

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flagmentManager = getSupportFragmentManager();

                dialogFragment = new WatchQuestionActivity.AlertDialogFragment();
                dialogFragment.show(flagmentManager, "send alert dialogue");
            }
        });

        /*ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

            }
        });*/

        imageButton = (Button)findViewById(R.id.image_button7);//画像を出力
        //isImage = intent.getStringExtra("Image");//画像のうむ

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
                intent.putExtra("Image", "anwer");
                startActivity(intent);
            }
        });

        /*imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ImageActivity.class);
                intent.putExtra("Image", "anwer");
                startActivity(intent);
            }
        });*/
    }
    //通報時のダイアログ
    public static class AlertDialogFragment extends DialogFragment {
        //選択肢のリスト
        private String[] menulist = {"通報する", "通報しない"};

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle saveInstanceState) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            //タイトル
            alert.setTitle("通報しますか?");
            alert.setItems(menulist, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //通報する
                    if (i == 0) {

                    }
                    //通報しない
                    else {
                        //
                    }
                }
            });
            return alert.create();
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

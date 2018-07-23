package com.example.tomoko.pro2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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

public class CheckQuestionActivity extends AppCompatActivity {

    TextView questioner;
    TextView questionNum;
    TextView answerNum;
    TextView equiNum;
    TextView questionText;
    Button helpButton;
    TextView statusText;
    TextView jobText;
    TextView belongText;


    private DialogFragment dialogFragment;
    private FragmentManager flagmentManager;

    User user;
    Client client;
    Activity activity;

    String question;

    Button imageButton;
    String isImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_question);

        client = (Client) this.getApplication();
        activity = this;
        user = client.getMyUser();


        questioner = (TextView) findViewById(R.id.ask_name_text_view);
        questionNum = (TextView) findViewById(R.id.asked_num);
        answerNum = (TextView) findViewById(R.id.answered_num);
        equiNum = (TextView) findViewById(R.id.equ_num);
        questionText = (TextView) findViewById(R.id.question_text_view);
        helpButton = (Button) findViewById(R.id.help_button);
        statusText = (TextView) findViewById(R.id.status_textview);
        jobText = (TextView) findViewById(R.id.job_text);
        belongText = (TextView) findViewById(R.id.belong_text);

        //Intent intent = getIntent();
        //各値を設定
        //String name = intent.getStringExtra("questioner");
        //String askNum = intent.getStringExtra("questiondN");
        //String ansNum = intent.getStringExtra("answerNum");
        //String equivalent = intent.getStringExtra("valueN");
        //final String question = intent.getStringExtra("Text");

        Question q = client.getWatchingQ();
        String name = q.getQuestioner().getName();
        String askNum = String.valueOf(q.getQuestioner().getQuestion());
        String ansNum = String.valueOf(q.getQuestioner().getAnswer());
        String equivalent = String.valueOf(q.getQuestioner().getValue());
        final String question = q.getQuestion();
        String status;
        int c = q.getCoin();

        if (q.checkOffered()) {
            status = q.getOffer().getName() + "さんへ直接オファー";
        } else {
           status = "お礼: " + c + "コイン";
        }


        questioner.setText(name);
        questionNum.setText(askNum);
        answerNum.setText(ansNum);
        equiNum.setText(equivalent);
        questionText.setText(question);
        statusText.setText(status);
        jobText.setText(q.getQuestioner().getJob());
        belongText.setText(q.getQuestioner().getBelong());

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagmentManager = getSupportFragmentManager();
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        toastMake(result, 0, -200);
                        return false;
                    }
                });
                client.Candidacy(question);
                //dialogFragment = new CheckQuestionActivity.AlertDialogFragment();
                //dialogFragment.show(flagmentManager, "help_alert_dialogue");


            }
        });

        imageButton = (Button)findViewById(R.id.image_button4);//画像を出力
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
                //intent.putExtra("Image", "question");
                startActivity(intent);
            }
        });
    }
    //あとで消す
    public static class AlertDialogFragment extends DialogFragment {
        //選択肢のリスト
        private String[] menulist = {"助ける", "助けない"};

        @Override
        @NonNull

        public Dialog onCreateDialog(Bundle saveInstanceState) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            //タイトル
            alert.setTitle("お助けしますか");
            alert.setItems(menulist, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //助ける
                    if (i == 0) {

                    }
                    //助けない
                    else {
                        //
                    }
                }
            });
            return alert.create();
        }
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

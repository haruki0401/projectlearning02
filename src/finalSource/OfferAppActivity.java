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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OfferAppActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView candidateCheckListView;
    //立候補を確認し、オファーを送信する
    User user;
    Client client;
    Activity activity;

    TextView questionText;

    int position;
    //Question question;
    ArrayList<User> candidates;
    Button turnDownButton;

    ArrayList<String> candidateNames;
    ArrayList<String> candidateAnswer;
    ArrayList<String> candidateQuestion;
    ArrayList<String> candidateValue;
    ArrayList<String> candidateBelong;
    ArrayList<String> candidateJob;
    ListView canditateListView;

    private DialogFragment dialogFragment;
    private FragmentManager flagmentManager;

    static String name;
    static String questionStr;
    static Activity act;
    static Question questionQ;
    static Client clients;

    Button imageButton;
    String isImage;

    String questionS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_app);

        client = (Client) this.getApplication();
        clients = (Client) this.getApplication();
        activity = this;
        user = client.getMyUser();

        Question q = client.getWatchingQ();
        act = this;
        candidateCheckListView = (ListView) findViewById(R.id.candidated_person_list_view);

        turnDownButton = (Button) findViewById(R.id.turn_down_que_button);

        questionQ = q;
        setTitle("立候補状況");

        Intent intent = getIntent();
        /*position = intent.getIntExtra("Position", 0);
        questionS = intent.getStringExtra("Question");*/
        //question = client.getMyQuestion().get(position);
        //questionQ = client.getMyQuestion().get(position);
        candidates = q.getCandidates();//立候補者を取得

        questionText = (TextView)findViewById(R.id.question_offer_text);
        questionS = q.getQuestion();
        questionText.setText(questionS);//質問内容を作成

        candidateNames = new ArrayList<>();
        candidateAnswer = new ArrayList<>();
        candidateQuestion = new ArrayList<>();
        candidateValue = new ArrayList<>();
        candidateBelong = new ArrayList<>();
        candidateJob = new ArrayList();

        ArrayList<String> items = new ArrayList<>();

        for (int i = 0; i < candidates.size(); i++) {
            User u = candidates.get(i);
            candidateNames.add(u.getName());
            candidateAnswer.add(String.valueOf(u.getAnswer()));
            candidateQuestion.add(String.valueOf(u.getQuestion()));
            candidateValue.add(String.valueOf(u.getValue()));
            candidateBelong.add(u.getBelong());
            candidateJob.add(u.getJob());

        }
        BaseAdapter adapter = new OfferAppAdapter(activity.getApplicationContext(), R.layout.offer_app_text_view,
                candidateNames, candidateAnswer, candidateQuestion, candidateValue, candidateJob, candidateBelong);


        candidateCheckListView.setAdapter(adapter);
        candidateCheckListView.setOnItemClickListener(this);
        turnDownButton.setVisibility(View.INVISIBLE);
        turnDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imageButton = (Button)findViewById(R.id.image_button5);//画像を出力
        //isImage = intent.getStringExtra("Image");//画像のうむ

        if(q.getIsImage()) {//画像が存在するならば
            imageButton.setVisibility(View.VISIBLE);
        } else {//画像が存在しないならば
            imageButton.setVisibility(View.INVISIBLE);//見えない
        }
        //imageButton.setVisibility(View.INVISIBLE);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ImageActivity.class);
                //intent.putExtra("Image", "question");
                startActivity(intent);
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //オファーする
        //flagmentManager = getSupportFragmentManager();

        //dialogFragment = new MoneyActivity.AlertDialogFragment2();
        //dialogFragment.show(flagmentManager, "offer alerat");
        name = candidateNames.get(position);
        client.setOnCallBack(new Client.CallBackTask() {
            public boolean CallBack(String result) {
                super.CallBack(result);
                if(result.equals("オファー成功")) {
                    toastMake("オファーを送信しました", 0, -200);

                } else {
                    toastMake("オファーの送信に失敗しました", 0, -200);
                }
                return false;
            }
        });
        client.sendOffer(questionS,name);
    }

    void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
    }


    public static class AlertDialogFragment extends DialogFragment {
        //選択肢のリスト
        private String[] menulist = {"オファーを送る", "オファーを送らない"};

        @Override
        @NonNull


        public Dialog onCreateDialog(Bundle saveInstanceState) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            //タイトル
            alert.setTitle("オファーを送信しますか");
            alert.setItems(menulist, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //送信する
                    if (i == 0) {
                        clients.sendOffer(questionQ.getQuestion(), name);

                    }
                    //送信しない
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

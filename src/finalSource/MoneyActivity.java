package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MoneyActivity extends AppCompatActivity {
    ImageButton homeButton;
    ImageButton talkButton;
    ImageButton noticeButton;
    ImageButton groupButton;
    Button sendButton;//送金ボタン
    Button receiveButton;//着金ボタン
    EditText sendMoney;//送金金額
    TextView receivePMoney;//着金金額

    LinearLayout checkLayout;
    TextView checkText;
    Button yesButton;
    Button noButton;

    private TextView textView;
    private DialogFragment dialogFragment;
    private FragmentManager flagmentManager;

    User user;
    private Client client;
    Activity activity;

    private int flag = 0;

    static Client c;
    static Activity a;
    static String sendCoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);



        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        homeButton = findViewById(R.id.home_button);
        noticeButton = findViewById(R.id.notice_button);
        talkButton = findViewById(R.id.talk_button);
        groupButton = findViewById(R.id.group_button);

        sendButton = findViewById(R.id.send_money_button);
        receiveButton = findViewById(R.id.receive_money_button);
        sendMoney = findViewById(R.id.send_money);

        checkLayout = (LinearLayout)findViewById(R.id.money_check_view);
        checkText = (TextView)findViewById(R.id.money_check_text);
        yesButton = (Button)findViewById(R.id.yes_button);
        noButton = (Button)findViewById(R.id.no_button);

        checkLayout.setVisibility(View.INVISIBLE);

        //送金ボタンが押された場合
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //送金金額を取得
                String coin = sendMoney.getText().toString();
                flag = 0;
                checkText.setText("[確認]\n" + coin + "コインを送金しますか?");
                yesButton.setText("送金します");
                noButton.setText("送金しません");
                checkLayout.setVisibility(View.VISIBLE);
            }
        });


        //着金ボタンが押された場合
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = 1;

                checkText.setText("[確認]\n コインを受け取りますか?");
                yesButton.setVisibility(View.VISIBLE);
                noButton.setVisibility(View.VISIBLE);
                yesButton.setText("着金します");
                noButton.setText("着金しません");
                checkLayout.setVisibility(View.VISIBLE);
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {//送金ならば
                    client.setOnCallBack(new Client.CallBackTask() {
                        public boolean CallBack(String result) {
                            super.CallBack(result);

                            toastMake(result, 0, -200);
                            checkLayout.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    });
                    client.sendCoin(Integer.parseInt(sendMoney.getText().toString()));
                } else if (flag == 1) {//着金ならば
                    client.setOnCallBack(new Client.CallBackTask() {
                        public boolean CallBack(String result) {
                            super.CallBack(result);

                            toastMake(result, 0, -200);
                            checkLayout.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    });
                    client.receiveCoin();
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 0) {
                    checkText.setText("送金が取りやめられました");

                } else if(flag == 1) {
                    checkText.setText("着金が取りやめられました");
                }
                yesButton.setVisibility(View.INVISIBLE);
                noButton.setVisibility(View.INVISIBLE);
            }
        });

        //ホームボタンが押されたらホーム画面へ
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                client.setListener(createListener());
                client.receiveUserInformation(user.getName());

            }
        });
        //グループボタンが押されたらグループ画面へ
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), GroupActivity.class);
                startActivity(intent);
            }
        });
        //質問管理ボタンが押されたら質問管理画面へ
        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TalkManageActivity.class);
                startActivity(intent);
            }
        });

        //お知らせボタンが押されたらお知らせ画面へ
        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), NoticeActivity.class);
                startActivity(intent);
            }
        });



    }


    void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
    }


    public void setTextView(String message) {
        textView.setText(message);
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

    private Client.Listener createListener() {

        return new Client.Listener() {

            @Override
            public void onSuccess(int f) {
                //情報取得成功ならば
                if(f == 0) {
                    Intent intent = new Intent(getApplication(), HomeActivity.class);
                    startActivity(intent);
                }
            }
        };
    }



}
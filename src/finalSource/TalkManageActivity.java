package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TalkManageActivity extends AppCompatActivity {
    ImageButton homeButton;
    ImageButton noticeButton;
    ImageButton moneyButton;
    ImageButton groupButton;

    Button answerAppButton;
    //Button answerCheckButton;
    Button offerCheckButton;
    Button ranCheckButton;

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_manage);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        homeButton = findViewById(R.id.home_button);
        noticeButton = findViewById(R.id.notice_button);
        moneyButton = findViewById(R.id.money_button);
        groupButton = findViewById(R.id.group_button);

        answerAppButton = findViewById(R.id.answer_app_button);
        //answerCheckButton = findViewById(R.id.answer_check_button);
        offerCheckButton = findViewById(R.id.offer_check_button);
        ranCheckButton = (Button)findViewById(R.id.ran_check_button);

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

        //お知らせボタンが押されたらお知らせ画面へ
        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), NoticeActivity.class);
                startActivity(intent);
            }
        });

        //お金管理が押されたら、お金管理画面へ
        moneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MoneyActivity.class);
                startActivity(intent);
            }
        });

        //立候補者を確認する//自分がした質問を確認する
        answerAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("取得成功")) {
                            Intent intent = new Intent(getApplication(), AnswerAppActivity.class);
                            startActivity(intent);
                            //toastMake(result, 0, -200);

                            //client.receiveUserInformation(userName, activity);
                            //Intent intent = new Intent(getApplication(), HomeActivity.class);
                            //startActivity(intent);
                        } else {
                            //toastMake(result, 0, -200);
                        }
                        return false;
                    }
                });

                client.receiveMyQuestion();

            }
        });

        //帰ってきた回答を確認する//これは消しても良いか？
        /*answerCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                client.setOnCallBack(new Client.CallBackTask() {
                    public void CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("取得成功")) {

                            //toastMake(result, 0, -200);

                            //client.receiveUserInformation(userName, activity);
                            //Intent intent = new Intent(getApplication(), HomeActivity.class);
                            //startActivity(intent);
                            Intent intent = new Intent(getApplication(), AnswerListActivity.class);
                            startActivity(intent);
                        } else {
                            //toastMake(result, 0, -200);
                        }
                    }
                });

                client.receiveAnswer(activity);

            }
        });*/

        //自分にきているオファーを確認する
        offerCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("取得成功")) {
                            Intent intent = new Intent(getApplication(), OfferCheckActivity.class);
                            startActivity(intent);

                        } else {

                        }
                        return false;
                    }
                });

                client.receiveOffer();//自分に来ているオファーを受け取る

            }
        });

        //自分が立候補した質問を確認する
        ranCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("取得成功")) {//取得成功ならば遷移
                            Intent intent = new Intent(getApplication(), AnswerListActivity.class);
                            startActivity(intent);
                        } else {

                        }
                        return false;
                    }
                });

                client.receiveCandidate();//自分に来ているオファーを受け取る

            }
        });
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

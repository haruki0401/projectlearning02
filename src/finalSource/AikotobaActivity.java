package com.example.tomoko.pro2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AikotobaActivity extends AppCompatActivity {
    EditText aikotoba;
    EditText name;

    Button sendAikotobaButton;

    User user;
    Client client;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aikotoba);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        aikotoba = (EditText)findViewById(R.id.aikotoba_text);
        name = (EditText)findViewById(R.id.name_text);

        sendAikotobaButton = (Button)findViewById(R.id.send_aikotoba_button);

        sendAikotobaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ai = aikotoba.getText().toString();//合言葉を取得
                String n = name.getText().toString();//名前を取得

                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {//パスワードを得られないならば
                        super.CallBack(result);
                        if(result.equals("合言葉が違います") || result.equals("パスワード取得失敗")) {
                            toastMake(result, 0, -200);
                        } else {//パスワードを得られたなら、パスワードを出力
                            aikotoba.setText(result);
                        }
                        return false;
                    }
                });
                client.remindPassword(n, ai);
            }
        });

    }
    void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
    }
}

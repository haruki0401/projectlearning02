package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText userNameText;
    EditText passwordText;
    Button loginButton;
    Button registButton;
    Button forgetButton;



    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        userNameText = findViewById(R.id.edit_username);
        passwordText = findViewById(R.id.edit_password);
        loginButton = findViewById(R.id.login_button);
        registButton = findViewById(R.id.register_cbutton);
        forgetButton = (Button)findViewById(R.id.forget_button);
        //user = new User();

        //ログインボタンが押されたらホームへ移動
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = userNameText.getText().toString();
                String password = passwordText.getText().toString();
                //user.setName(userName);//今は入力された文字列を取得しているが、以後はすでに登録された文字列の名前を持つはず
                //user.setPassword(password);

                //Intent intent = new Intent(getApplication(), HomeActivity.class);
                //startActivity(intent);
                //client.loginRequest(userName,password);

                client.setListener(createListener());
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("ログイン成功")) {
                            toastMake(result, 0, -200);

                            client.receiveUserInformation(userName);
                            //Intent intent = new Intent(getApplication(), HomeActivity.class);
                            //startActivity(intent);
                        } else {
                            toastMake(result, 0, -200);
                        }
                        return false;
                    }
                });

                client.loginRequest(userName, password);
                //Intent intent = new Intent(getApplication(), HomeActivity.class);
                //startActivity(intent);


            }
        });

        //登録ボタンが押されたら登録画面へ移動
        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), AikotobaActivity.class);
                startActivity(intent);
            }
        });

    }
/*
    protected void onStop(){
        super.onStop();
        finish();
    }*/

    void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
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

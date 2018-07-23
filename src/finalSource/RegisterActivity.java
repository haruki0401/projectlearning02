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

public class RegisterActivity extends AppCompatActivity {
    EditText userNameText;
    EditText passwordText;
    EditText passwordText2;//password for check
    Button registerButton;

    //private String fileName = "userfile.txt";

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        userNameText = findViewById(R.id.edit_username);
        passwordText = findViewById(R.id.edit_password);
        passwordText2 = findViewById(R.id.edit_passwordforcheck);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //入力された文字列を取得
                final String userName = userNameText.getText().toString();
                String password = passwordText.getText().toString();
                String aikotoba = passwordText2.getText().toString();



                client.setListener(createListener());
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("登録成功")) {
                            toastMake(result, 0, -200);
                            client.receiveUserInformation(userName);

                        }
                        toastMake(result,0,-200);
                        return false;
                    }
                });
                client.accountRequest(userName, password, aikotoba);

                //Intent intent = new Intent(getApplication(), RegistUserActivity.class);

                //saveFile(fileName, userName);
                //user.setName(userName);
                //user.setPassword(password);

                //client.accountRequest(userName, password, password2, activity);
                //startActivity(intent);
            }
        });
    }

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
                    //toastMake("情報取得", 0, -200);
                    Intent intent = new Intent(getApplication(), RegistUserActivity.class);
                    startActivity(intent);
                }
            }
        };
    }


}

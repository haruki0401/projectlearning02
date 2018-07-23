package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectActivity extends AppCompatActivity {
    User user;
    Client client;
    Activity activity;
    EditText portNum;
    EditText ipAdress;
    Button connectButton;
    //Button gologintButton;
    private ProgressBar progressBar;
    //UserSupport us;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        client = (Client) this.getApplication();
        activity = this;
        user = client.getMyUser();

        //portNum = (EditText) findViewById(R.id.port_number_edittext);
        //ipAdress = (EditText) findViewById(R.id.IP_address_edittext);
        connectButton = (Button) findViewById(R.id.connect_button);
        //gologintButton = (Button) findViewById(R.id.go_login_button);

        //gologintButton.setVisibility(gologintButton.INVISIBLE);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);



        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View view) {
                try {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    //接続に成功したならば
                    //String ip = ipAdress.getText().toString();
                    //String por = portNum.getText().toString();
                    // client.setListener(createListener());

                    client.setOnCallBack(new Client.CallBackTask() {
                        public boolean CallBack(String result) {
                            super.CallBack(result);
                            if (result.equals("サーバと接続できました")) {
                                toastMake(result, 0, -200);
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                //Intent intent = new Intent(getApplication(), MainActivity.class);//変更点
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                toastMake(result, 0, -200);
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                            return false;
                        }
                    });
                    client.connectServer();
                } catch(Exception e) {//あとで
                    toastMake("入力が正しくありません", 0, -200);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }

    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
    }




}

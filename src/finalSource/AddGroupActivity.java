package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddGroupActivity extends AppCompatActivity {
    private Button addGroupButton;
    private EditText groupName;
    private EditText groupEx;

    User user;
    Client client;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        addGroupButton = findViewById(R.id.add_group_button);
        groupName = findViewById(R.id.group_name_edittext);
        groupEx = findViewById(R.id.group_expl_edittext);

        //追加ボタンが押された場合 //入力された内容を取得
        addGroupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String gName = groupName.getText().toString();
                String gEx = groupEx.getText().toString();
                client.setListener(createListener());
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        if(result.equals("作成成功")) {
                            toastMake(result, 0, -200);
                            //user.addGroupl(gName);
                            client.receiveUserInformation(user.getName());
                            //Intent intent = new Intent(getApplication(), HomeActivity.class);
                            //startActivity(intent);
                        } else {
                            toastMake(result, 0, -200);
                        }
                        return false;
                    }
                });


                client.creatGroup(gName,gEx);
            }
        });

        //改行禁止
        groupEx.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return false;
                }
                return false;
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

    private Client.Listener createListener() {

        return new Client.Listener() {

            @Override
            public void onSuccess(int f) {
                //情報取得成功ならば
                if(f == 0) {
                    Intent intent = new Intent(getApplication(), GroupActivity.class);
                    startActivity(intent);
                }
            }
        };
    }


}

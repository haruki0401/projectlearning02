package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Locale;

public class AskQuestionActivity extends AppCompatActivity {

    Button attachButton;
    Button sendQuestionButton;
    TextView questionTextView;
    EditText questionEditText;
    TextView teachersName;
    EditText coinEditText;

    TextView imageText;

    //参考URL用
    //TextView webText;
    //Button webButton;

    User user;
    Client client;
    Activity activity;
    private int coin;
    private String teacher;

    String group;

    boolean isImage;//画像を添付するかどうか

    //Bitmap bmp;//画像を入れておく
    byte[] images = null;//画像を入れておく

    private static final int RESULT_PICK_IMAGEFILE = 1000;//リクエストを識別する「要求コード」


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Intent intent = getIntent();
        int isTeacher = intent.getIntExtra("teacher",1);
        group = intent.getStringExtra("group");

//        setTitle(client.getMyGroup().getgname() + " : 質問作成");

        isImage = false;//はじめは添付しない

        attachButton = (Button)findViewById(R.id.attach_button);
        sendQuestionButton = (Button)findViewById(R.id.send_button);
        questionTextView = (TextView)findViewById(R.id.question_text_view);
        questionEditText = (EditText)findViewById(R.id.question_edit_text);
        teachersName = (TextView)findViewById(R.id.ask_question);
        coinEditText = (EditText)findViewById(R.id.how_much_coin);
        imageText = (TextView)findViewById(R.id.image_text);

        //参考URL用
        //webText = (EditText)findViewById(R.id.web_textview);
        //webButton = (Button)findViewById(R.id.url_button);


        if(isTeacher == 0) {//先生指定ならば
            //coin = 300;
            teacher = intent.getStringExtra("Text");
            String text = teacher + "さんに質問する";
            //String text2 = coin + "コインで質問を送信する";
            teachersName.setText(text);
            //sendQuestionButton.setText(text2);
        } else {//先生未指定ならば
            teacher = null;
            //coin = 200;
            //String text = coin + "コインで質問を送信する";
            //sendQuestionButton.setText(text);
        }


        sendQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                String question = questionEditText.getText().toString();
                //掛けコインを取得
                String coins = coinEditText.getText().toString();
                if(coins.equals("")) {
                    toastMake("コインを入力してください", 0, -200);
                } else {
                    coin = Integer.parseInt(coinEditText.getText().toString());
                    //client.setListener(createListener());
                    client.setOnCallBack(new Client.CallBackTask() {
                        public boolean CallBack(String result) {
                            super.CallBack(result);
                            if (result.equals("送信成功")) {
                                toastMake("質問を送信しました", 0, -200);

                                client.setOnCallBack(new Client.CallBackTask() {
                                    public boolean CallBack(String result) {
                                        super.CallBack(result);
                                        if (result.equals("取得成功")) {
                                            Intent intent = new Intent();

                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }

                                        return false;
                                    }


                                });
                                client.receiveGroupInformation(group);


                            } else {
                                toastMake("質問の送信に失敗しました", 0, -200);
                            }
                            return false;
                        }
                    });
                }

                client.sendQuestion(question, group, teacher, coin, isImage, images);
            }
        });
        //改行禁止
        /*questionEditText.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return false;
                }
                return false;
            }
        });*/





        //添付用
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });
        //openBrouwer
        /*webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(webText.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try {
                    Bitmap bmp = getBitmapFromUri(uri);//一応画面に表示
                    //imageView.setImageBitmap(bmp);

                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    images = stream.toByteArray();

                    String s = "画像添付済み";
                    imageText.setText(s);
                    isImage = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    /*@Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            client.setOnCallBack(new Client.CallBackTask() {
                public boolean CallBack(String result) {
                    super.CallBack(result);

                    return AskQuestionActivity.super.onKeyDown(keyCode,event);
                }
            });
            client.receiveGroupInformation(group);
        }
        return AskQuestionActivity.super.onKeyDown(keyCode,event);
    }*/

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}

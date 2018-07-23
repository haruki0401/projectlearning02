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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

public class ReturnAnswerActivity extends AppCompatActivity {
    TextView questionerNameTextView;
    TextView questionContentTextView;
    EditText returnAnswerEditText;
    Button rejectButton;
    Button attachButton;
    Button returnButton;
    TextView oreitext;

    String questioner;
    String question = "どうやってこの問題を解けば良いでしょう";
    String answer;

    User user;
    Client client;
    Activity activity;

    Button imageButton;
    String isImage;

    TextView imageText;

    String isImageA;//画像を添付するかどうか

    Bitmap bmp;//画像を入れておく

    private static final int RESULT_PICK_IMAGEFILE = 1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_answer);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Question q = client.getWatchingQ();

        //Intent intent = getIntent();
        question = q.getQuestion();//intent.getStringExtra("Text");//質問内容を取得
        questioner = q.getQuestioner().getName();//intent.getStringExtra("Teacher");//質問者


        questionerNameTextView = findViewById(R.id.questioner_name_text_view);
        questionContentTextView = findViewById(R.id.question_content_text_view);
        returnAnswerEditText = findViewById(R.id.return_text);
        rejectButton = findViewById(R.id.reject_button);
        attachButton = findViewById(R.id.attach2_button);
        returnButton = findViewById(R.id.return_answer_button);
        oreitext = (TextView)findViewById(R.id.orei_text);

        String s = questioner + "さんからの質問";
        questionerNameTextView.setText(s);
        questionContentTextView.setText(question);
        String str = "お礼: " + String.valueOf(q.getCoin());
        oreitext.setText(str);
        //拒否した場合
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        toastMake(result, 0, -200);
                        return false;
                    }

                });

                client.refuseOffer(question);//オファーを拒否する

            }
        });
        //添付ボタン
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //送信ボタン
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //文字列を取得
                answer = returnAnswerEditText.getText().toString();

                client.setOnCallBack(new Client.CallBackTask() {
                    public boolean CallBack(String result) {
                        super.CallBack(result);
                        toastMake(result, 0, -200);

                        return false;
                    }
                });

                /*if(q.getIsImage()) {//画像が添付されているならば
                    //client.sendQuestion(question, answer, isImageA);

                } else {//画像の添付がないならば
                    client.sendAnswer(question, answer);//回答を送信
                }*/
                client.sendAnswer(question, answer);//回答を送信

            }
        });

        imageButton = (Button)findViewById(R.id.image_button6);//画像を出力
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
                intent.putExtra("Image", "question");
                startActivity(intent);
            }
        });
        attachButton.setVisibility(View.INVISIBLE);
        //添付用
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // it would be "*/*".
                //intent.setType("image/*");
                intent.setType("*/*");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData.getData() != null) {

                ParcelFileDescriptor pfDescriptor = null;
                try {
                    Uri uri = resultData.getData();
                    // Uriを表示
                    String s = "添付画像:" + uri.toString();
                    imageText.setText(s);
                    isImage = "yesImage";//画像あり
                    pfDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                    if (pfDescriptor != null) {
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                        //imageView.setImageBitmap(bmp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (pfDescriptor != null) {
                            pfDescriptor.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    ImageView image;

    User user;
    Client client;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();

        Question q = client.getWatchingQ();

        image = (ImageView)findViewById(R.id.image_view);

        Intent intent = getIntent();
        byte[] bytes = q.getImage();
        Bitmap bmp2= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        image.setImageBitmap(bmp2);
        //String s = intent.getStringExtra("Image");
        //String filename = intent.getStringExtra("File");//画像のファイル名を取得


        /*try {//画像を内部ストレージから取り出す
            //String fileName = "sample.jpg";  // "data/data/[パッケージ名]/files/sample.jpg" になる
            //Bitmap bitmap = loadBitmapLocalStorage(fileName, this);  //this は起動した Activity が良い(Context)
            Bitmap bitmap;
            if(s.equals("question")){//質問への添付
                bitmap = client.getWatchingQ().getImageQ();
               } else {//回答への添付
                 bitmap = client.getWatchingQ().getImageA();
               }
                  image.setImageBitmap(bitmap);
        } catch (IOException e) {
         e.printStackTrace();
      }
*/
    }

    /*public static final Bitmap loadBitmapLocalStorage(String fileName, Context context)
            throws IOException, FileNotFoundException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(context.openFileInput(fileName));
            return BitmapFactory.decodeStream(bis);
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();//エラーを表示
                //IOException, NullPointerException
            }
        }
    }*/

}

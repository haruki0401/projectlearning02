package com.example.tomoko.pro2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class ImagePutctivity extends AppCompatActivity {

    private static final int RESULT_PICK_IMAGEFILE = 1000;//リクエストを識別する「要求コード」
    Button button2;
    ImageView imageView;
    ImageView imageView2;

    User user;
    Client client;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_putctivity);

        imageView = findViewById(R.id.image_view);
        imageView2 = findViewById(R.id.image_view2);

        client = (Client)this.getApplication();
        activity = this;
        user = client.getMyUser();



        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//端末画像フォルダ起動
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });
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
                    byte[] bytes=stream.toByteArray();


                    client.setbyte(bytes);
                    client.setOnCallBack(new Client.CallBackTask() {
                        public boolean CallBack(String result) {
                            super.CallBack(result);
                            if(result.equals("受信成功")) {
                                //toastMake(result, 0, -200);
                                byte[] receiveB = client.getbyte();
                                Bitmap bmp3= BitmapFactory.decodeByteArray(receiveB,0,receiveB.length);
                                imageView2.setImageBitmap(bmp3);
                                //Intent intent = new Intent(getApplication(), HomeActivity.class);
                                //startActivity(intent);
                            } else {
                                //toastMake(result, 0, -200);
                            }
                            return false;
                        }
                    });
                    client.sendImage();


                    //Bitmap bmp2= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    //imageView.setImageBitmap(bmp2);


                    /*int num= ByteBuffer.wrap(bytes).getInt();
                    System.out.println(num);*/


                    //send(bytes);

                    System.out.println("送信完了");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}

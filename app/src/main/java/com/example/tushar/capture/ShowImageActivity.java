package com.example.tushar.capture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;

public class ShowImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        Bitmap btImage;
        ImageView image = (ImageView) findViewById(R.id.image);
        Button getInfo = (Button) findViewById(R.id.getInfo);
        Button cancel = (Button) findViewById(R.id.cancel);
        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("imageUri"));
        Log.d("ShowImageActivity", uri.toString());
        if(uri != null) {
            try {
                btImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                image.setImageBitmap(btImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "image will be sent to server and response will be shown in another activity", Toast.LENGTH_LONG).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });


    }
}

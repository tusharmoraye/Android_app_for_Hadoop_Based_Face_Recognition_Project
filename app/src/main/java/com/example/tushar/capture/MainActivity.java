package com.example.tushar.capture;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_IMAGE_CAPTURE = 1;
    private int PICK_IMAGE_REQUEST = 2;

    File storagePath = null;
    String imageFileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        Button galleryButton = (Button) findViewById(R.id.galleryButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = null;
                try {
                    file = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                File file = null;
                try {
                    file = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private File createImageFile() throws IOException {
        if(storagePath == null) {
            storagePath = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/capture/images/");
            if(!storagePath.exists()) storagePath.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "/image_" + timeStamp + "_.jpg";
        File image = new File(storagePath, imageFileName);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            File imageFile = copyFileFromUri(getApplicationContext(), uri);


            Intent intent = new Intent(this, ShowImageActivity.class);
            intent.putExtra("imageUri", Uri.fromFile(new File(storagePath.getAbsolutePath() + imageFileName)).toString());
            startActivity(intent);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, ShowImageActivity.class);
            intent.putExtra("imageUri", Uri.fromFile(new File(storagePath.getAbsolutePath() + imageFileName)).toString());
            startActivity(intent);
        }
    }

    public File copyFileFromUri(Context context, Uri fileUri)
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File outputFile = null;

        try
        {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);

            outputFile = createImageFile();
            outputStream = new FileOutputStream( outputFile); // filename.png, .mp3, .mp4 ...
            if(outputStream != null){
                Log.e( "copyFromUri", "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ( ( bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0 )
            {
                outputStream.write( buffer, 0, buffer.length );
            }
        } catch ( Exception e ){
            Log.e( "copyFromUri", "Exception occurred " + e.getMessage());
        } finally{

        }
        return outputFile;
    }

}

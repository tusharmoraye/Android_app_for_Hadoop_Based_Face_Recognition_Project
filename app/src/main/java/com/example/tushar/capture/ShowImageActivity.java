package com.example.tushar.capture;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import org.apache.http.entity.mime.HttpEntity;

//import org.apache.http.HttpEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ShowImageActivity extends AppCompatActivity {

    Bitmap btImage = null;
    ProgressBar progressBar;
    TextView txtPercentage;
    Boolean check = true;
    int totalSize = 0;
    String filePath;
    String ServerUploadPath = "http://192.168.1.134/FaceRec/image_upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ImageView image = (ImageView) findViewById(R.id.image);
        Button getInfo = (Button) findViewById(R.id.getInfo);
        Button cancel = (Button) findViewById(R.id.cancel);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("imageUri"));
        filePath = uri.getPath();

        Log.d("ShowImageActivity", uri.toString());
        Log.d("ShowImageActivity", uri.getPath());

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

                //uploadMultipart();
                new UploadFileToServer().execute();
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

    void uploadMultipart() {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        btImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] byteArrayVar = byteArrayOutputStream.toByteArray();
//        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
//
//        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                //progressBar
//            }
//
//            @Override
//            protected void onPostExecute(String string1) {
//                super.onPostExecute(string1);
//            }
//
//            @Override
//            protected String doInBackground(Void ... params) {
//                ImageProcessClass imageProcessClass = new ImageProcessClass();
//                HashMap<String, String> HashMapParams = new HashMap<>();
//                HashMapParams.put("image_string", ConvertImage);
//                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
//                return FinalData;
//            }
//        }
//
//        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
//        AsyncTaskUploadClassOBJ.execute();

    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ServerUploadPath);

            try {

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                //entity.addPart("website", new StringBody("www.androidhive.info"));
                //entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = (int)entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("response", "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}




/*
File image = "....";
FileBody fileBody = new FileBody(image);
MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                         .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                         .addTextBody("params", "{....}")
                         .addPart("my_file", fileBody);
HttpEntity multiPartEntity = builder.build();

String url = "....";
HttpPost httpPost = new HttpPost(url);
httpPost.setEntity(multiPartEntity);
 */
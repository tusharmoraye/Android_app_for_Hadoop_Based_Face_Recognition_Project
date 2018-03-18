package com.example.tushar.capture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultActivity extends AppCompatActivity {



    ImageView  imageView;
    boolean stopThread;
    String src;
    Handler uiHandler;
    Bitmap myBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        imageView = (ImageView) findViewById(R.id.image);
        TextView nameView = (TextView) findViewById(R.id.name);
        TextView addressView = (TextView) findViewById(R.id.address);
        TextView crimesView = (TextView) findViewById(R.id.crimes);


        String url = "http://" + ShowImageActivity.IP;



        Intent intent = getIntent();
        String response = intent.getStringExtra("response");

        String allCrimes = "";

        try {

            JSONObject obj = new JSONObject(response);

            JSONObject user = obj.getJSONObject("output");

            int userid = user.getInt("uid");
            String name = user.getString("name");
            String address = user.getString("address");
            String image_path = user.getString("image_path");

            //JSONObject crimes = obj.getJSONObject("crimes");


            ///*
            JSONArray jsonArray = obj.getJSONArray("crimes");
            Log.d("imageProcessing", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                int cid = jsonobject.getInt("cid");
                int uid = jsonobject.getInt("uid");
                String title = jsonobject.getString("title");
                String discription = jsonobject.getString("discription");

                allCrimes += "\n\nTitle : \n" + title + "\n\nDescription : \n" + discription + "\n";
            }

            nameView.setText("Name : " + name);
            addressView.setText("Address : " + address);
            crimesView.setText("Crimes : \n" + allCrimes);
            src = url + "/" + image_path;

            Log.d("src", src);
            //getImageFromURL();

            new DownloadImage().execute(src);

            //imageView.setImageBitmap(getBitmapFromURL(url + "/" + image_path));

            //*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /*
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    */



    void getImageFromURL() // begins listening for any incoming data from the Arduino
    {
        stopThread = false;

        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    //Log.d("Thread", "Inside new thread");
                    try {
                        java.net.URL url = new java.net.URL(src);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        InputStream input = connection.getInputStream();
                        myBitmap = BitmapFactory.decodeStream(input);
                        if(myBitmap == null) {
                            Log.d("myBitmap", "it's NULL");
                        } else {
                            Log.d("myBitmap", "It's not NULL");
                        }
                        //imageView.setImageBitmap(myBitmap);
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(myBitmap);
                            }
                        });
                        stopThread = true;
                    } catch(IOException e) {
                        e.printStackTrace();
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }


    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            imageView.setImageBitmap(result);
            // Close progressdialog
        }
    }

}

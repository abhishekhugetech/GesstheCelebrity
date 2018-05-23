package com.example.rohan.gessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURL = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int celebChosen = 0;
    ImageView img;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public class DownloadTask extends AsyncTask<String,Void,String >{

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    };

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {

            try{
                URL url = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                return bmp;
            }catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct ! " ,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong it was " + celebNames.get(celebChosen),Toast.LENGTH_SHORT).show();
        }
        newCelebrity();
    }

    public void newCelebrity(){
        Random random = new Random();
        celebChosen = random.nextInt(celebURL.size());

        ImageDownloader imageDownloader = new ImageDownloader();
        Bitmap bmp = null;
        try {
            bmp = imageDownloader.execute(celebURL.get(celebChosen)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        img.setImageBitmap(bmp);


        locationOfCorrectAnswer = random.nextInt(4);
        int locationOfIncorrectAnswer;
        for (int i = 0;i<4;i++){

            if(i == locationOfCorrectAnswer){
                answers[i] = celebNames.get(celebChosen);
            }else{
                locationOfIncorrectAnswer = random.nextInt(celebNames.size());
                while (locationOfIncorrectAnswer == locationOfCorrectAnswer){
                    locationOfIncorrectAnswer = random.nextInt(celebNames.size());
                }
                answers[i] = celebNames.get(locationOfIncorrectAnswer);
            }


            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        String result = "";
        img = findViewById(R.id.celebImage);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()){
                celebURL.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()){
                celebNames.add(m.group(1));
            }
            newCelebrity();

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}

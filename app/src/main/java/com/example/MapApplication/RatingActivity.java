package com.example.MapApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RatingActivity extends AppCompatActivity {

    private RatingTask mRatingTask;
    private String[] ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String filename = "userinfo";
        SharedPreferences preferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
        boolean ratingflag = preferences.getBoolean("Rating", false);
        if(ratingflag){
            ((RatingBar)(findViewById(R.id.RatingBar1))).setRating(Float.valueOf(preferences.getString("Rating0","1")));
            ((RatingBar)(findViewById(R.id.RatingBar2))).setRating(Float.valueOf(preferences.getString("Rating1","1")));
            ((RatingBar)(findViewById(R.id.RatingBar3))).setRating(Float.valueOf(preferences.getString("Rating2","1")));
            ((RatingBar)(findViewById(R.id.RatingBar4))).setRating(Float.valueOf(preferences.getString("Rating3","1")));
            ((RatingBar)(findViewById(R.id.RatingBar5))).setRating(Float.valueOf(preferences.getString("Rating4","1")));
            ((EditText)(findViewById(R.id.RatingComment))).setText(preferences.getString("Rating5","hogehoge"));
        }

        ratings = new String[6];

        findViewById(R.id.RatingSubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // サーバに送信するデータの一部
                ratings[0] = String.valueOf((int)((RatingBar)findViewById(R.id.RatingBar1)).getRating());
                ratings[1] = String.valueOf((int)((RatingBar)findViewById(R.id.RatingBar2)).getRating());
                ratings[2] = String.valueOf((int)((RatingBar)findViewById(R.id.RatingBar3)).getRating());
                ratings[3] = String.valueOf((int)((RatingBar)findViewById(R.id.RatingBar4)).getRating());
                ratings[4] = String.valueOf((int)((RatingBar)findViewById(R.id.RatingBar5)).getRating());
                ratings[5] = ((EditText)(findViewById(R.id.RatingComment))).getText().toString();
                for(int i=0,l=ratings.length; i<l; i++){
                    System.out.println(String.format("RatingData%d: %s", i, ratings[i]));
                }

                String[] strings = new String[1];
                strings[0] = getString(R.string.URL_AddRating);
                for(int i=0,l=strings.length; i<l; i++){
                    System.out.println(String.format("TaskData%d: %s", i, strings[i]));
                }

                mRatingTask = new RatingTask(RatingActivity.this);
                mRatingTask.execute(strings);
            }
        });
    }

    class RatingTask extends AsyncTask<String, String, JSONObject> {
        private Context context;

        public RatingTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            System.out.println("==========  onPostExecute  ==========");
            super.onPostExecute(jsonObject);

            try {
                Boolean f = jsonObject.getBoolean("flag");
                if(f){
                    // ユーザ評価済みのフラグを設定する
                    String filename = "userinfo";
                    SharedPreferences preferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("Rating", true);
                    for(int i=0,l=ratings.length; i<l; i++){
                        editor.putString(String.format("Rating%d",i), ratings[i]);
                    }
                    editor.commit();

                    // TODO string.xmlから表示する文字列を取得する
                    Toast.makeText(context, "アプリの評価が完了しました", Toast.LENGTH_SHORT).show();
                    System.out.println(":::::  JSON success  :::::");
                } else {
                    Toast.makeText(context, "JSON ERROR", Toast.LENGTH_SHORT).show();
                    System.out.println(":::::  JSON error  :::::");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            System.out.println("==========  doInBackground  ==========");
            for(int i=0,l=params.length; i<l; i++){
                System.out.println(String.format("DATA %d: %s", i, params[i]));
            }

            String filename = "userinfo";
            SharedPreferences preferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
            String userid = String.valueOf(preferences.getInt("UserID", -1));
            System.out.println(userid);

            HttpURLConnection connection = null;
            DataOutputStream os = null;
            BufferedReader br = null;
            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                // TODO データ作成
                String postData = "";
                postData += "UserID=" + URLEncoder.encode(userid, "UTF-8");
                postData += "&" + "RatingMap=" + URLEncoder.encode(ratings[0], "UTF-8");
                postData += "&" + "RatingRLike=" + URLEncoder.encode(ratings[1], "UTF-8");
                postData += "&" + "RatingRComment=" + URLEncoder.encode(ratings[2], "UTF-8");
                postData += "&" + "RatingRDate=" + URLEncoder.encode(ratings[3], "UTF-8");
                postData += "&" + "RatingTravel=" + URLEncoder.encode(ratings[4], "UTF-8");
                postData += "&" + "RatingComment=" + URLEncoder.encode(ratings[5], "UTF-8");

                // データを送信する
                os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(postData);

                // レスポンスを受信する
                int iResponseCode = connection.getResponseCode();

                // 接続が確立したとき
                if (iResponseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder resultBuilder = new StringBuilder();
                    String line = "";

                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    // レスポンスの読み込み
                    while ((line = br.readLine()) != null) {
                        resultBuilder.append(String.format("%s%s", line, "\r\n"));
                    }
                    String result = resultBuilder.toString();

                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject;
                }
                // 接続が確立できなかったとき
                else {
                    System.out.println("not connected");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                // 開いたら閉じる
                try {
                    if (br != null) br.close();
                    if (os != null) { os.flush(); os.close(); }
                    if (connection != null) connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}

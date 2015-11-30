package com.example.MapApplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
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

public class RatingMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private RatingAverageTask mRatingAverageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_menu);
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

        findViewById(R.id.btnRatingDo).setOnClickListener(this);
        findViewById(R.id.btnRatingWatch).setOnClickListener(this);

        String[] strings = new String[1];
        strings[0] = getString(R.string.URL_SearchRatingAverage);
        mRatingAverageTask = new RatingAverageTask(this);
        mRatingAverageTask.execute(strings);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        switch (id) {
            case R.id.btnRatingDo: System.out.println("::: Rating Do Submit :::");
                intent = new Intent(RatingMenuActivity.this, RatingActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRatingWatch: System.out.println("::: Rating Watch Submit :::");
                intent = new Intent(RatingMenuActivity.this, RatingWatchActivity.class);
                startActivity(intent);
                break;
        }
    }

    class RatingAverageTask extends AsyncTask<String, String, JSONObject> {
        private Context context;
        private ProgressDialog dialog;

        public RatingAverageTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("アプリの総合評価得点の取得中です");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            System.out.println("==========  onPostExecute  ==========");
            super.onPostExecute(jsonObject);

            try {
                Boolean f = jsonObject.getBoolean("flag");
                if(f){
                    float point = Float.valueOf(jsonObject.getString("data"));
                    ((TextView) (findViewById(R.id.RatingPointView))).setText(String.format("%.1f", point));

                    Toast.makeText(context, "アプリの総合評価の表示が完了しました", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println(":::::  JSON error  :::::");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            System.out.println("==========  doInBackground  ==========");
            for(int i=0,l=params.length; i<l; i++){
                System.out.println(String.format("DATA %d: %s", i, params[i]));
            }

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

package com.example.MapApplication;

import android.content.Context;
import android.os.AsyncTask;
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

public class UpdatePasswordTask extends AsyncTask<String, String, JSONObject> {

    private Context mContext;
    private String mURL;

    public UpdatePasswordTask(Context mContext, String mURL) {
        super();
        this.mContext = mContext;
        this.mURL = mURL;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            Boolean f = jsonObject.getBoolean("flag");
            // TODO string.xmlから表示させる文字列を持ってくる
            if(f){
                Toast toast = Toast.makeText(mContext, "ok", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(mContext, "no", Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpURLConnection connection = null;
        DataOutputStream os = null;
        BufferedReader br = null;

        try {
            URL url = new URL(mURL);

            connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // データ作成
            String postData = "";
            postData += "nowEmail=" + params[0];
            postData += "&" + "oldPassword=" + params[1];
            postData += "&" + "newPassword=" + params[2];

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
                Toast toast = Toast.makeText(mContext, "not connected", Toast.LENGTH_LONG);
                toast.show();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (os != null) {
                    os.flush();
                    os.close();
                }
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}

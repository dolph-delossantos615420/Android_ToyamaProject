package com.example.MapApplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

public class RatingWatchActivity extends AppCompatActivity {

    private RatingWatchTask mRatingWatchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_watch);
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

        String[] strings = new String[1];
        strings[0] = getString(R.string.URL_SearchRatingWatch);
        mRatingWatchTask = new RatingWatchTask(this);
        mRatingWatchTask.execute(strings);
    }

    class RatingWatchTask extends AsyncTask<String, String, JSONObject> {
        private Context context;
        private ProgressDialog dialog;

        public RatingWatchTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            System.out.println("==========  onPreExecute  ==========");
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("アプリの評価コメントの取得中です");
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
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    System.out.println(jsonArray.length() + " : " + jsonArray);

                    List<ListItem> items = new ArrayList<ListItem>();
                    for(int i=0,l=jsonArray.length(); i<l; i++){
                        JSONArray jsona = jsonArray.getJSONArray(i);
                        items.add( new ListItem(jsona.get(0).toString(), jsona.get(1).toString(), jsona.get(2).toString()) );
                    }

                    ListItemAdapter adapter = new ListItemAdapter(context, items);
                    ((ListView)(findViewById(R.id.RatingCommentList))).setAdapter(adapter);

                    Toast.makeText(context, "アプリの評価コメントの表示が完了しました", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "JSON ERROR", Toast.LENGTH_SHORT).show();
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

        class ViewHolder {
            TextView ratingDate;
            TextView ratingVersion;
            TextView ratingComment;
        }

        // 表示するリストアイテム単体のクラス
        class ListItem {
            String sDate = "";
            String sVersion = "";
            String sComment = "";
            public ListItem(String sDate, String sVersion, String sComment) {
                this.sDate = sDate;
                this.sVersion = sVersion;
                this.sComment = sComment;
            }
        }

        class ListItemAdapter extends ArrayAdapter<ListItem> {
            // レイアウトファイルからViewオブジェクトを生成するため
            LayoutInflater mInflater;

            ListItemAdapter(Context context, List<ListItem> items) {
                super(context, 0, items);
                // インフレイターを取得
                mInflater = getLayoutInflater();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // ListItemオブジェクトを取得
                ListItem item = getItem(position);

                ViewHolder holder;
                // convertViewには使いまわすためのViewがあれば入っている
                if (convertView == null) {
                    // ない場合はレイアウトファイルから生成する
                    convertView = mInflater.inflate(R.layout.rating_listitem, null);
                    // ViewHolderも作って
                    holder = new ViewHolder();
                    // 参照をセット
                    holder.ratingDate = (TextView) convertView.findViewById(R.id.rating_date);
                    holder.ratingVersion = (TextView) convertView.findViewById(R.id.rating_version);
                    holder.ratingComment = (TextView) convertView.findViewById(R.id.rating_comment);
                    // ViewHolderを使いまわせるようにセットしておく
                    convertView.setTag(holder);
                } else {
                    // ある場合はViewHolderを取り出して再利用
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.ratingDate.setText(item.sDate);
                holder.ratingVersion.setText(item.sVersion);
                holder.ratingComment.setText(item.sComment);
                // 表示するViewを返す
                return convertView;
            }
        }

    }
}

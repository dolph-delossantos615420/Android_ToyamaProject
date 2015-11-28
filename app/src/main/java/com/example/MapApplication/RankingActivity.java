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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RankingActivity extends AppCompatActivity {

    private RankingTask mRankingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        String flag = bundle.getString("SearchTag");
        int id = bundle.getInt("ButtonTag");

        String[] strings = new String[2];
        strings[0] = getResources().getString(R.string.URL_SearchRanking);
        System.out.println( strings[0] );
        switch (id) {
            case R.id.btnLikeRanking:
                strings[1] = getString(R.string.SearchTag_RankingLike);
                System.out.println( strings[1] );
                mRankingTask = new RankingTask(this);
                mRankingTask.execute(strings);
                break;
            case R.id.btnCommentRanking:
                strings[1] = getString(R.string.SearchTag_RankingComment);
                System.out.println( strings[1] );
                mRankingTask = new RankingTask(this);
                mRankingTask.execute(strings);
                break;
            case R.id.btnCreatetimeRanking:
                strings[1] = getString(R.string.SearchTag_RankingDate);
                System.out.println( strings[1] );
                mRankingTask = new RankingTask(this);
                mRankingTask.execute(strings);
                break;
        }
    }


    class RankingTask extends AsyncTask<String, String, JSONObject> {
        private Context context;
        private ProgressDialog dialog;

        public RankingTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            System.out.println("==========  onProgressUpdate  ==========");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            System.out.println("==========  onPreExecute  ==========");
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("読み込み中...");
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
                    System.out.println(":::::  JSON SUCCESS  :::::");
                    System.out.println(jsonArray.length() + " : " + jsonArray);

                    // データを作る
                    List<ListItem> items = new ArrayList<ListItem>();
                    for(int i=0,l=jsonArray.length(); i<l; i++){
                        JSONArray jsona = jsonArray.getJSONArray(i);
                        items.add( new ListItem(R.drawable.ic_launcher, jsona.get(0).toString(), jsona.get(1).toString()) );
                    }

                    // 自前のアダプターを作って
                    ListItemAdapter adapter = new ListItemAdapter(context, items);

                    // アダプターをセット
                    ((ListView)findViewById(R.id.RankingList)).setAdapter(adapter);
                } else {
                    Toast.makeText(context, "JSON ERROR", Toast.LENGTH_SHORT).show();
                    System.out.println(":::::  JSON ERROR  :::::");
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
                String postData = "SearchTag=" + params[1];

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


        class ViewHolder {
            ImageView imageView; // アイコン画像の表示用
            TextView nameTextView; // ユーザ名の表示用
            TextView commentTextView; // コメントの表示用
        }

        // 表示するリストアイテム単体のクラス
        class ListItem {
            int rank;
            String placename;
            String remark;
            public ListItem(int rank, String placename, String remark) {
                this.rank = rank;
                this.placename = placename;
                this.remark = remark;
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
                    convertView = mInflater.inflate(R.layout.ranking_listitem, null);
                    // ViewHolderも作って
                    holder = new ViewHolder();
                    // 参照をセット
                    holder.imageView = (ImageView) convertView.findViewById(R.id.rank_img);
                    holder.nameTextView = (TextView) convertView.findViewById(R.id.rank_name);
                    holder.commentTextView = (TextView) convertView.findViewById(R.id.rank_remark);
                    // ViewHolderを使いまわせるようにセットしておく
                    convertView.setTag(holder);
                } else {
                    // ある場合はViewHolderを取り出して再利用
                    holder = (ViewHolder) convertView.getTag();
                }
                // アイコン画像をセット
                holder.imageView.setImageResource(item.rank);
                // ユーザ名をセット
                holder.nameTextView.setText(item.placename);
                // コメントをセット
                holder.commentTextView.setText(item.remark);
                // 表示するViewを返す
                return convertView;
            }
        }

    }
}

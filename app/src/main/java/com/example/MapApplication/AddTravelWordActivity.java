package com.example.MapApplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

public class AddTravelWordActivity extends Activity {

    private AppendTravelWordTask mAppendTravelWordTask;
    private Spinner mSpinner;
    private EditText mEditText;
    private int REQUEST_CODE = 1;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_travel_word);

        mSpinner = (Spinner)findViewById(R.id.sprTravelWordCategory);
        mEditText = (EditText)findViewById(R.id.edtCutline);
    }

    public void btnTravelWordAdd(View view) {
        // サーバにアップロードする情報等
        String[] SubmitData = new String[3];
        SubmitData[0] = String.valueOf(mSpinner.getSelectedItemPosition() + 1);
        SubmitData[1] = mEditText.getText().toString();
        SubmitData[2] = path;

        // 未入力の項目が存在する場合は処理を終了する
        for(int i=1; i<3; i++){
            if(SubmitData[i] == "" || SubmitData[i] == null){
                return;
            }
        }

        // サーバと非同期通信を行うクラスをインスタンスする
        mAppendTravelWordTask = new AppendTravelWordTask(this);
        mAppendTravelWordTask.execute(SubmitData);
    }

    public void onClickGallery(View view) {
        // TODO 画像ファイルをギャラリーから選択する
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ギャラリーで画像が選択されなかった場合は処理を終了する
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK || data == null) {
            path = "";
            return;
        }

        // TODO 画像のパスを取得する
        String id = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            id = DocumentsContract.getDocumentId(data.getData());
            String selection = "_id=?";
            String[] selectionArgs = new String[]{id.split(":")[1]};
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.MediaColumns.DATA},
                    selection, selectionArgs,
                    null);
            if (cursor.moveToFirst()) {
                path = cursor.getString(0);
            }
            cursor.close();
        } else {
            String[] columns = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), columns, null, null, null);
            if (cursor.moveToFirst()) {
                path = cursor.getString(0);
            }
            cursor.close();
        }
    }

    class AppendTravelWordTask extends AsyncTask<String, String, String> {
        private Context context;

        public AppendTravelWordTask(Context context) {
            System.out.println("==========  AppendTravelWordTask  ==========");
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            System.out.println("==========  onPreExecute  ==========");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("==========  onPostExecute  ==========");
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            System.out.println("==========  onProgressUpdate  ==========");
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            System.out.println("==========  doInBackground  ==========");
            for(int i=0,l=params.length; i<l; i++){
                System.out.println(String.format("DATA %d: %s", i, params[i]));
            }

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0]);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            MultipartEntity entity = new MultipartEntity((HttpMultipartMode.BROWSER_COMPATIBLE));

            File file = new File(params[2]);
            FileBody fileBody = new FileBody(file);
            entity.addPart("upfile", fileBody);

            // 画像以外のデータを送る場合はaddTextBodyを使う
            entity.addPart("CategoryID", new StringBody(params[0], ContentType.APPLICATION_JSON));
            entity.addPart("Comment", new StringBody(params[1], ContentType.APPLICATION_JSON));

            httpPost.setEntity(entity);

            try {
                String result = httpClient.execute(httpPost, responseHandler);
                System.out.println(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

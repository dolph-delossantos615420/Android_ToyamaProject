package com.example.MapApplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserPasswordUpdateActivity extends AppCompatActivity {

    private UpdatePasswordTask mUpdatePasswordTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_password_update);
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

        Button btnPasswordUpdate = (Button)findViewById(R.id.btnPasswordUpdate);
        btnPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            EditText edtEmail = (EditText)findViewById(R.id.edtEmail);
            EditText edtOldPassword = (EditText)findViewById(R.id.edtOldPassword);
            EditText edtNewPassword = (EditText)findViewById(R.id.edtNewPassword);

            String[] strArray = new String[3];
            strArray[0] = edtEmail.getText().toString();
            strArray[1] = edtOldPassword.getText().toString();
            strArray[2] = edtNewPassword.getText().toString();

            mUpdatePasswordTask = new UpdatePasswordTask(UserPasswordUpdateActivity.this, getString(R.string.URL_PasswordUpdate));
            mUpdatePasswordTask.execute(strArray);
            }
        });

    }

}

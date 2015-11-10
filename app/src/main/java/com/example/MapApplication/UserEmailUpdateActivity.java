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

public class UserEmailUpdateActivity extends AppCompatActivity {

    private UpdateEmailTask mUpdateEmailTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_email_update);
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

        Button btnEmailUpdate = (Button)findViewById(R.id.btnEmailUpdate);
        btnEmailUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtPassword = (EditText)findViewById(R.id.edtPassword);
                EditText edtOldEmail = (EditText)findViewById(R.id.edtOldEmail);
                EditText edtNewEmail = (EditText)findViewById(R.id.edtNewEmail);

                String[] strArray = new String[3];
                strArray[0] = edtPassword.getText().toString();
                strArray[1] = edtOldEmail.getText().toString();
                strArray[2] = edtNewEmail.getText().toString();

                mUpdateEmailTask = new UpdateEmailTask(UserEmailUpdateActivity.this, getString(R.string.URL_EmailUpdate));
                mUpdateEmailTask.execute(strArray);

                Toast toast = Toast.makeText(UserEmailUpdateActivity.this, "EmailUpdate", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

}

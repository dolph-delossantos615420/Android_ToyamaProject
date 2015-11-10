package com.example.MapApplication;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddTravelWordActivity extends Activity {

    private Spinner mSpinner;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_travel_word);

        mSpinner = (Spinner)findViewById(R.id.sprTravelWordCategory);
        mEditText = (EditText)findViewById(R.id.edtCutline);

        Button btnTravelWordAdd = (Button)findViewById(R.id.btnTravelWordAdd);
        btnTravelWordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strArray = new String[2];
                strArray[0] = String.valueOf(mSpinner.getSelectedItemPosition() + 1);
                strArray[1] = mEditText.getText().toString();

                for(int i=0; i<strArray.length; i++){ System.out.println(strArray[i]); }

            }
        });
    }

}

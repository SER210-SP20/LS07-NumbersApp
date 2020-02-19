package edu.quinnipiac.ser210.ls07_numberapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String yearFact = (String) getIntent().getExtras().get("yearFact");

        TextView textView = (TextView) findViewById(R.id.result);

        textView.setText(yearFact);

    }
}

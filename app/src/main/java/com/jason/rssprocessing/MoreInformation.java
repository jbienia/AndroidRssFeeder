package com.jason.rssprocessing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MoreInformation extends AppCompatActivity {
String url;
    Intent newIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_information);

        TextView description = (TextView)findViewById(R.id.tvDescription);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        description.setText(intent.getStringExtra("description"));
         newIntent = new Intent(this,Web_View.class);
    }

    public void onClick(View v)
    {
        //Intent newIntent = new Intent(MoreInformation.this,Web_View.class);
        newIntent.putExtra("url",url);
        startActivity(newIntent);
    }
}

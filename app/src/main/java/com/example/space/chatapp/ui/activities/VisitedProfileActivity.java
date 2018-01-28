package com.example.space.chatapp.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.space.chatapp.R;

public class VisitedProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_profile);
      //  String visitUid=getIntent().getExtras().get("visit").toString();
        //Toast.makeText(VisitedProfileActivity.this,visitUid,Toast.LENGTH_SHORT).show();
    }
}

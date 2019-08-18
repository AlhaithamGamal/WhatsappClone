package com.example.mywhatsappclone;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
private  String getUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getUserId = getIntent().getExtras().get("visit_user_id").toString();
        Toast.makeText(this,"USER ID:"+getUserId,Toast.LENGTH_LONG).show();

    }
}

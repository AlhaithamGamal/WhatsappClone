package com.example.mywhatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterationActivity extends AppCompatActivity {
    private Button register;
    private EditText userEmail;
    private EditText userPassword;
    private TextView alreadyHaveAccount;
    private Toolbar mtoolbr;
    private FirebaseAuth mAuth;
    private DatabaseReference rootReference;
    private ProgressDialog progressBar;
    String email;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        mAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        mtoolbr = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbr);

        initialization();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("WhatsupH");
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void createNewAccount() {
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {

            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();

        } else {
            progressBar.setTitle("Creating new account");
            progressBar.setMessage("Please wait while creating your account....");
            progressBar.setCanceledOnTouchOutside(true);
            progressBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String currentUserId = mAuth.getCurrentUser().getUid();
                        rootReference.child("Users").child("currentUserId").setValue("");
                        sendUserToMainActivity();
                        Toast.makeText(RegisterationActivity.this, "Account Created Successfully ....", Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterationActivity.this, "Exception error:" + message, Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();
                    }
                }
            });


        }
    }


    private void initialization() {
        register = (Button) findViewById(R.id.reg_button);
        userEmail = (EditText) findViewById(R.id.reg_email);
        userPassword = (EditText) findViewById(R.id.reg_password);
        alreadyHaveAccount = (TextView) findViewById(R.id.already_have_an_account);
        progressBar = new ProgressDialog(this);

    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterationActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", userEmail.getText().toString());
        outState.putString("password", userPassword.getText().toString());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        email = savedInstanceState.getString("username");
        password = savedInstanceState.getString("password");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
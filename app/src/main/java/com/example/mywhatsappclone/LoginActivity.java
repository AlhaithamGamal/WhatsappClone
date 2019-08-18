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

public class LoginActivity extends AppCompatActivity {
   // private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Button loginButton;
    private Toolbar mtoolbr;
    private EditText userEmail;
    private EditText userPassword;
    private TextView forgetPassword;

    private TextView needAccount;
    private Button phoneButton;
    String email;
    String password;
    ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference();

       // currentUser = mAuth.getCurrentUser();
  //     mtoolbr = (Toolbar) findViewById(R.id.main_page_toolbar);
   //   setSupportActionBar(mtoolbr);
        initialization();
//    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   // getSupportActionBar().setHomeButtonEnabled(true);
      loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               allowUserLogin();
            }
        });
      needAccount.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              sendUserToRegisterationnActivity();
          }
      });
      phoneButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
              startActivity(intent);
          }
      });


    }


    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
     //   mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
     //   finish();
    }
    private void sendUserToRegisterationnActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterationActivity.class);
       //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
//        finish();
    }


    private void initialization(){

        loginButton = (Button)findViewById(R.id.login_button);
        phoneButton = (Button)findViewById(R.id.phone_button);
        forgetPassword=(TextView)findViewById(R.id.forget_password_link);
        needAccount = (TextView)findViewById(R.id.need_new_account);
        userEmail = (EditText)findViewById(R.id.login_email);
        userPassword = (EditText)findViewById(R.id.login_password);
        progressBar = new ProgressDialog(this);

    }
    private void allowUserLogin(){
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {

            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();

        }
        else{
                progressBar.setTitle("SignIn");
                progressBar.setMessage("Signing in please wait...");
                progressBar.setCanceledOnTouchOutside(true);
                progressBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            sendUserToMainActivity();
                            Toast.makeText(LoginActivity.this,"Signed in welcome !",Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();
                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this,"Exception error:"+message,Toast.LENGTH_SHORT).show();
                            progressBar.dismiss();
                        }


                    }
                });
        }
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

        outState.putString("username",userEmail.getText().toString());
        outState.putString("password",userPassword.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        email = savedInstanceState.getString("username");
        password = savedInstanceState.getString("password");
        super.onRestoreInstanceState(savedInstanceState);
    }


    public void forgetPassword(View view) {


        Intent intent = new Intent(LoginActivity.this,ForgetPassword.class);
        startActivity(intent);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "Password sent", Toast.LENGTH_LONG).show();
                            }
                            else{

                                Toast.makeText(getApplicationContext(),"Exception"+task.getException(),Toast.LENGTH_LONG).show();
                            }

                        }
                    });



    }
}

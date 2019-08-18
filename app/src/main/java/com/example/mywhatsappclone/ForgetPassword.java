package com.example.mywhatsappclone;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
FirebaseAuth auth;
    EditText ema;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ema=findViewById(R.id.reset_email);
        auth = FirebaseAuth.getInstance();
    }

    public void forget(View view) {
        String email = ema.getText().toString();
        auth.sendPasswordResetEmail(email)
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

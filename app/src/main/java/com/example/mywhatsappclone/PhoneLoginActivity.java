package com.example.mywhatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private Button sendVerificationCode;
    private Button verifyButton;
    private EditText inputPhoneNumber, inputVerificationCode;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode = (Button) findViewById(R.id.send_ver_code_button);
        verifyButton = (Button) findViewById(R.id.ver_button);
        inputPhoneNumber = (EditText)findViewById(R.id.phone_number_input);
        inputVerificationCode = (EditText)findViewById(R.id.verification_code_input);
        progressDialog = new ProgressDialog(PhoneLoginActivity.this);
        sendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = inputPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {

                    Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number:", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.setTitle("Phone Verification");
                    progressDialog.setMessage("Please wait thile authenticating your phone");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);
                    // OnVerificationStateChangedCallbacks


                }
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);
                String verificationCode= inputVerificationCode.getText().toString();
                if(TextUtils.isEmpty(verificationCode)){

                    Toast.makeText(PhoneLoginActivity.this,"Please write verification code first",Toast.LENGTH_SHORT).show();

                }
                else{
                    progressDialog.setTitle(" Verification code");
                    progressDialog.setMessage("Please wait while verifying your code");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                sendVerificationCode.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                verifyButton.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);
                Toast.makeText(PhoneLoginActivity.this,"Invalid phone number write again with code country"+e.getMessage().toString(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                progressDialog.dismiss();
                sendVerificationCode.setVisibility(View.INVISIBLE);
                verifyButton.setVisibility(View.VISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);

                Toast.makeText(PhoneLoginActivity.this,"Code sent",Toast.LENGTH_SHORT).show();

                // ...
            }
        };


    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(PhoneLoginActivity.this,"Congratulation Verified..",Toast.LENGTH_SHORT).show();
                            sendToMainActivity();

                            // ...
                        } else {
                            String message = task.getException().getMessage().toString();
                            Toast.makeText(PhoneLoginActivity.this,"Something happened !"+message,Toast.LENGTH_SHORT).show();

                        }
                    }

                });
    }

    private void sendToMainActivity() {
        Intent intent = new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.example.mywhatsappclone;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {
    Button updateProfile;
    EditText userStatus, userName;
    String currentUserId;
    private static final int GALLERY_PIC = 1;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    private Toolbar mtoolbr;
    ProgressDialog progressDialos;
    private StorageReference userProfileImageReference;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mtoolbr = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbr);
        getSupportActionBar().setTitle("WhatsupH");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference();
        userProfileImageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");

        initialize();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
        Toast.makeText(settings.this, "RETRIEVING DATA ", Toast.LENGTH_LONG).show();
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PIC);


            }
        });
      //  userName.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"WELCOME"+userName.getText().toString(), Toast.LENGTH_SHORT).show();
        retrieveData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // mAuth.signOut();
                // sendUserToLoginActivity();
                super.onBackPressed();
                return true;
            case R.id.logout:
                mAuth.signOut();
                sendUserToLoginActivity();
            case R.id.create_group:
                requestNewGroup();

        }
        return super.onOptionsItemSelected(item);
    }

    private void retrieveData() {
        mRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))) {
                    String retrieveName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveImage = dataSnapshot.child("image").getValue().toString();
                    userName.setText(retrieveName);
                    userName.setEnabled(false);
                    userStatus.setText(retrieveStatus);
                 //   Picasso.get().load(retrieveImage).into(circleImageView); //retrieve image from database and view it in circular view
                    Glide.with(circleImageView.getContext())
                            .load(retrieveImage)
                            .into(circleImageView);
                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                    String retrieveName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    userName.setText(retrieveName);
                    userName.setEnabled(false);
                    userStatus.setText(retrieveStatus);
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp));
                } else {
                    userName.setVisibility(View.VISIBLE);
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp));
                    Toast.makeText(settings.this, "Please enter your info first then upload your profile pic ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSettings() {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();
        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(settings.this, "please enter username", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setUserStatus)) {
            Toast.makeText(settings.this, "please enter status ", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            mRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(settings.this, "SAVED SUCCESSFULLY !!!", Toast.LENGTH_SHORT).show();


                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(settings.this, "EXCEPTION" + message, Toast.LENGTH_LONG).show();

                    }
                }
            });
        }


    }

    private void initialize() {
        updateProfile = (Button) findViewById(R.id.setting_button);
        userStatus = (EditText) findViewById(R.id.set_status);
        userName = (EditText) findViewById(R.id.set_username);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        progressDialos = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PIC && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialos.setTitle("Set profile image");
                progressDialos.setMessage("Please wait while your profile image is updating...");
                progressDialos.setCanceledOnTouchOutside(true);
                progressDialos.show();

                Uri resultUri = result.getUri();
                StorageReference filePath = userProfileImageReference.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressDialos.dismiss();

                            Toast.makeText(settings.this, "Profile image updated successfully...", Toast.LENGTH_LONG).show();
                         // final String downloadurI = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                            String downloadUrl = task.getResult().getDownloadUrl().toString();
                            mRef.child("Users").child(currentUserId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialos.dismiss();
                                        Toast.makeText(settings.this,"Image saved successfully into database!",Toast.LENGTH_LONG).show();

                                    }
                                    else{progressDialos.dismiss();
                                        String message = task.getException().getMessage().toString();
                                        Toast.makeText(settings.this,"ERROR"+message,Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                        } else {
                            String message = task.getException().getMessage().toString();
                            progressDialos.dismiss();
                            Toast.makeText(settings.this, "Exception" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(settings.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(settings.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();


    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(settings.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(settings.this);
        groupNameField.setHint("e.g Coding Cafe");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(settings.this, "Please write Group Name...", Toast.LENGTH_SHORT).show();
                } else {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroup(final String groupName) {
        mRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(settings.this, groupName + " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

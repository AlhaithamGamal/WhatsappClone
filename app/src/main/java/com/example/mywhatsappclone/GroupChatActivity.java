package com.example.mywhatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 2;
    private static final int RC_SIGN_IN = 1;
    ListView lstView;
    MessagesCustomAdapter messagesArrayAdapter;
    ArrayList messages = new ArrayList();
    String currentGroupName, currentUserId, currentUserName, currentDate, currentTime;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    DatabaseReference mMessageDatabaseReference;
    DatabaseReference groupNameRef;
    DatabaseReference groupMessageKeyRef;
    private ScrollView scrollViewGroup;
    private EditText groupMessage;
    private ImageButton groupImageButton, photoPickerButton;
    private TextView displayMessageGroup;
    private Toolbar toolbarGroup;
    private ChildEventListener mChildEventListener;
    private int color;
    private StorageReference mChatPhotosStorageReferences;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
     ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("Groups").child(currentGroupName);
        currentUserId = mAuth.getCurrentUser().getUid();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReferences = mFirebaseStorage.getReference();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        initialization();
        getUserInfo();


        photoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RC_PHOTO_PICKER);

            }
        });

        groupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabase();
                groupMessage.setText("");
//               automatic scrool down messages
            }
        });


    }


    private void saveMessageInfoToDatabase() {
        String message = groupMessage.getText().toString();
        String messageKey = groupNameRef.push().getKey(); //create a key
        if (TextUtils.isEmpty(message)) {

            Toast.makeText(GroupChatActivity.this, "Please enter message here ....", Toast.LENGTH_SHORT).show();

        } else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey); //created hash map to allow revcieve keys
            groupMessageKeyRef = groupNameRef.child(messageKey);
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);
            //     messages.add(new Messages(currentUserName,message,currentDate,currentTime));


        }

    }

    private void getUserInfo() {
        final String name = "name";
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialization() {
        toolbarGroup = (Toolbar) findViewById(R.id.group_chat_bar_layout_toolbar);
        setSupportActionBar(toolbarGroup);
        getSupportActionBar().setTitle(currentGroupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        lstView = findViewById(R.id.recv);

        groupMessage = (EditText) findViewById(R.id.write_message_group);
        photoPickerButton = findViewById(R.id.photoPickerButton);
        groupImageButton = (ImageButton) findViewById(R.id.send_message_group);
//        displayMessageGroup = (TextView) findViewById(R.id.view_message_group);
//        scrollViewGroup = (ScrollView) findViewById(R.id.scrollview_group_chat);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu_two, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.logout:
                mAuth.signOut();
                sendUserToLoginActivity();
                break;
            case R.id.clear:
                //      clearChat(currentUserName);
            case R.id.clear_group:
                clearGroup();
        }
        return true;
    }

    private void clearGroup() {
        groupNameRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                messagesArrayAdapter.clear();

            }
        });

    }


    private void clearChat(final String currentUserName) {
        String key = groupNameRef.push().getKey();
        Query query = groupNameRef.orderByChild("id").equalTo(currentUserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.child("id").getValue().equals(currentUserId)) {
                        HashMap<String, Object> messageInfoMap = new HashMap<>();
                        messageInfoMap.put("name", currentUserName);
                        messageInfoMap.put("message", "Message Removed");
                        messageInfoMap.put("date", currentDate);
                        messageInfoMap.put("time", currentTime);
                        groupMessageKeyRef.updateChildren(messageInfoMap);
                        ds.getRef().updateChildren(messageInfoMap);


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {

                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {


               displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

          displayMessages(dataSnapshot);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             displayMessages(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String charDate = ((DataSnapshot) iterator.next()).getValue().toString();
            String charMessage = ((DataSnapshot) iterator.next()).getValue().toString();
            String charName = ((DataSnapshot) iterator.next()).getValue().toString();
            String charTime = ((DataSnapshot) iterator.next()).getValue().toString();
            messages.add(new Messages(null, charMessage, charName, charDate, charTime));



        }


//        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//            String charDate = String.valueOf(ds.child("date").getValue());
//            String charMessage = String.valueOf(ds.child("message").getValue());
//            String charId = String.valueOf(ds.child("id").getValue());
//            String charName = String.valueOf(ds.child("name").getValue());
//            String charTime = String.valueOf(ds.child("time").getValue());
//            messages.add(new Messages(charName, charMessage, charDate, charTime, charId, null));
        messagesArrayAdapter = new MessagesCustomAdapter(GroupChatActivity.this, messages);
        lstView.setAdapter(messagesArrayAdapter);
        lstView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lstView.setSelection(messagesArrayAdapter.getCount() - 1);
            }
        });

    }


    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(GroupChatActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendToSettings() {
        Intent setintent = new Intent(GroupChatActivity.this, settings.class);
        setintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setintent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color", color);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }


    //==================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) { // in order when press exit prevent exit lloop and exit from app
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
            progressDialog.show();
          //  messagesArrayAdapter.clear();
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = mChatPhotosStorageReferences.child("chat_photos/*" + System.currentTimeMillis());
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            //messagesArrayAdapter.clear();
                            // Set the download URL to the message box, so that the user can send it to the database

                            Messages friendlyMessage = new Messages(uri.toString(), null, currentUserName, currentDate, currentTime);
                            mMessageDatabaseReference.push().setValue(friendlyMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "Photo sent successfully", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Photo URI FAILED", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed Uploading to storage", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "ERROR Request recieve :", Toast.LENGTH_LONG).show();
        }

    }
}
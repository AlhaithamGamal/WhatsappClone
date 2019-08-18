package com.example.mywhatsappclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbr;
    private ViewPager vpager;

    private TabLayout tblay;
    private TabAccessAdapter myTabAccessAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private DatabaseReference rootReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //  currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        mtoolbr = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbr);
        getSupportActionBar().setTitle("KnowledgeOverFlowApp");
        vpager = (ViewPager) findViewById(R.id.main_tab_pager);
        myTabAccessAdapter = new TabAccessAdapter(getSupportFragmentManager());
        vpager.setAdapter(myTabAccessAdapter);
        tblay = (TabLayout) findViewById(R.id.main_tab);
        tblay.setupWithViewPager(vpager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {

            sendUserToLoginActivity();
        } else {
          //  Toast.makeText(this, "Verifying", Toast.LENGTH_LONG).show();
            VerifyUserExistance();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(this,"RESTAAART",Toast.LENGTH_SHORT).show();
    }

    private void VerifyUserExistance()
    {
     final String  currentUserId = mAuth.getCurrentUser().getUid();

        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome Again", Toast.LENGTH_LONG).show();
                }
                else
                {
                    sendToSettings();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(loginIntent);
        } else {
            startService(loginIntent);
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                mAuth.signOut();
                sendUserToLoginActivity();

            case R.id.logout:
                mAuth.signOut();
                sendUserToLoginActivity();
            case R.id.create_group:
                requestNewGroup();
                return true;
            case R.id.profile:
                sendToSettings();
            case R.id.find_friends:
                findFriends();
        }
        return true;
    }

    private void findFriends() {
        Intent intent = new Intent(MainActivity.this,FindFriends.class);
        startActivity(intent);
    }

    private void requestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Coding Cafe");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Group Name...", Toast.LENGTH_LONG).show();
                }
                else
                {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }



    private void createNewGroup(final String groupName)
    {
        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName + " group is Created Successfully...", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void sendToSettings() {
        Intent setintent = new Intent(MainActivity.this, settings.class);
        // setintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setintent);
        //   finish();


    }

 /*   @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }*/

  /*  @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uid",currentUserId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentUserId = savedInstanceState.getString("uid");
    }


}

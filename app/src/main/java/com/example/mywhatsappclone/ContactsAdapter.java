package com.example.mywhatsappclone;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends ArrayAdapter<Contacts> {
    Context context;
    ArrayList<Contacts>contacts;

    ContactsAdapter(Context context , ArrayList<Contacts> contacts){
        super(context,0,contacts);
        this.context = context;
        this.contacts = contacts;

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contacts contacts = getItem(position);
        if (convertView == null) {


            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.user_display_layout, null, true);

        }

        TextView userName = convertView.findViewById(R.id.user_name);
        userName.setText(contacts.getName());
        TextView userStatus = convertView.findViewById(R.id.user_status);
        userStatus.setText(contacts.getStatus());
        CircleImageView profileImage = convertView.findViewById(R.id.profile_image);
        Picasso.get().load(contacts.getImage()).placeholder(R.drawable.profile_image).into(profileImage);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        return convertView;
    }
}

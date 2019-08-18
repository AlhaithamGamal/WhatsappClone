package com.example.mywhatsappclone;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesCustomAdapter extends ArrayAdapter<Messages> {
    ArrayList<Messages> messagesArrayList;
    Context context;
    FirebaseAuth mAtuh = FirebaseAuth.getInstance();
    String currentUserId = mAtuh.getCurrentUser().getUid();

    MessagesCustomAdapter(Context context, ArrayList<Messages> messages) {
        super(context, 0, messages);
        this.messagesArrayList = messages;
        this.context = context;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Messages message = getItem(position);

        if (convertView == null) {


                LayoutInflater layoutInflater = (LayoutInflater) getContext().
                        getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = layoutInflater.inflate(R.layout.row_messagesright, null, true);

        }


        TextView messages = convertView.findViewById(R.id.messages);
        messages.setText(message.getMessages());
        TextView name = convertView.findViewById(R.id.name);
        name.setText(message.getName());
        TextView date = convertView.findViewById(R.id.date);
        date.setText(message.getDate());
        TextView time = convertView.findViewById(R.id.time);
        time.setText(message.getTime());
        ImageView photoView = convertView.findViewById(R.id.photoImageView);
        boolean isPhoto = message.getImage() != null;
        if (isPhoto) {
            messages.setVisibility(View.GONE);
            photoView.setVisibility(View.VISIBLE);
//            Picasso.get().
//                    load(message.getImage()).into(photoView);
            Glide.with(photoView.getContext())
              .load(message.getImage())
               .into(photoView);
        } else {
            messages.setVisibility(View.VISIBLE);
            messages.setText(message.getMessages());
            photoView.setVisibility(View.GONE);

        }
        return convertView;


    }
}

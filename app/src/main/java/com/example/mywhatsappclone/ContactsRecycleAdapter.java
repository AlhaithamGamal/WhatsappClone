package com.example.mywhatsappclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsRecycleAdapter extends RecyclerView.Adapter<ContactsRecycleAdapter.MyViewHolder> {

    private Context mContext;
    private List<Contacts> contacts;


    ContactsRecycleAdapter(Context mContext, List<Contacts> contacts) {
        this.mContext = mContext;
        this.contacts = contacts;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.user_display_layout, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


       Contacts cont = contacts.get(position);

        holder.userName.setText(cont.getName());
        holder.userStatus.setText(cont.getStatus());
        Picasso.get().load(cont.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView userName;
        TextView userStatus;
       CircleImageView profileImage;



        public MyViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.user_profile_image);


        }
    }
}

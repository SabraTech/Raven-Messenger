package com.example.space.chatapp.ui.adapters;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.space.chatapp.R;
import com.example.space.chatapp.models.User;

import java.util.List;

public class UserListingRecyclerOldAdapter extends RecyclerView.Adapter<UserListingRecyclerOldAdapter.ViewHolder> {

    private List<User> users;

    public UserListingRecyclerOldAdapter(List<User> users) {
        this.users = users;
    }

    public void add(User user) {
        users.add(user);
        notifyItemInserted(users.size() - 1);
    }
    private void setProfileImage(ImageView profileImage , String img){
        try {
            if (img.equals("default")) {
                profileImage.setImageResource(R.drawable.default_avata);
            }
            else {
                Bitmap src;
                byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Drawable d = new BitmapDrawable(src);
                profileImage.setImageDrawable(d);
            }

        }catch (Exception e){
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //set to new item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_user2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListingRecyclerOldAdapter.ViewHolder holder, int position) {
        User user = users.get(position);

        if (user.getEmail() != null) {
            holder.txtUsername.setText(user.getEmail());
           //holder.txtStatus.setText();
            setProfileImage(holder.imageViewProfile ,user.getAvatar());
        }

        //when click on this item -> go to his profile
        holder.holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent otherProfileIntent= new Intent(AllUsersActivity, VisitedProfileActivity.class);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    public User getUser(int position) {
        return users.get(position);
    }




    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtStatus, txtUsername;
        private ImageView imageViewProfile ;
        private View holderView ;

        ViewHolder(View itemView) {
            super(itemView);
           // txtStatus = itemView.findViewById(R.id.all_users_status);
            txtUsername = itemView.findViewById(R.id.all_users_username);
            imageViewProfile=itemView.findViewById(R.id.all_users_profile_image);
            holderView=itemView.findViewById(R.id.one_item);
        }
    }
}

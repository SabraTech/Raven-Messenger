package com.example.space.chatapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewAllUsers;
    private UserListingRecyclerAdapter userListingRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //map it to all users activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        //get tool bar and set title all users the back arrow is setted from manifest
        toolbar = findViewById(R.id.all_users_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        // swipeRefreshLayout.setOnRefreshListener(AllUsersActivity);

        //get recycler view and set attributes to it
        recyclerViewAllUsers = findViewById(R.id.all_users_recycler_view);
        recyclerViewAllUsers.setHasFixedSize(true);
        recyclerViewAllUsers.setLayoutManager(new LinearLayoutManager(this));

        getUsers();

    }

    /*
    this function get user from database and put them on recycler view
     */
    private void getUsers() {

        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<>();
                while (dataSnapshotIterator.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshotIterator.next();
                    User user = dataSnapshotChild.getValue(User.class);
                    Log.e(AllUsersActivity.class.getName(), "uid:: " + user.getUid());
                    if (!TextUtils.equals(user.getUid(), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }

                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

                userListingRecyclerAdapter = new UserListingRecyclerAdapter(users);
                recyclerViewAllUsers.setAdapter(userListingRecyclerAdapter);
                userListingRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                Toast.makeText(AllUsersActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    /*
    adapter class
    I define it here to move between activities
     */
    public class UserListingRecyclerAdapter extends RecyclerView.Adapter<UserListingRecyclerAdapter.ViewHolder> {

        private List<User> users;

        public UserListingRecyclerAdapter(List<User> users) {
            this.users = users;
        }

        public void add(User user) {
            users.add(user);
            notifyItemInserted(users.size() - 1);
        }

        private void setProfileImage(ImageView profileImage, String img) {
            try {
                if (img.equals("default")) {
                    profileImage.setImageResource(R.drawable.default_avata);
                } else {
                    Bitmap src;
                    byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                    src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Drawable d = new BitmapDrawable(src);
                    profileImage.setImageDrawable(d);
                }

            } catch (Exception e) {
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //set to new item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_user2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            User user = users.get(position);

            if (user.getEmail() != null) {
                holder.txtUsername.setText(user.getEmail());
                //holder.txtStatus.setText();
                setProfileImage(holder.imageViewProfile, user.getAvatar());
            }

            //when click on this item -> go to his profile
            holder.holderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String visitUserId = userListingRecyclerAdapter.getUser(position).getUid();
                    Intent visitedProfileIntent = new Intent(AllUsersActivity.this, VisitedProfileActivity.class);
                    visitedProfileIntent.putExtra("visit", visitUserId);
                    startActivity(visitedProfileIntent);
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


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtStatus, txtUsername;
            ImageView imageViewProfile;
            View holderView;

            ViewHolder(View itemView) {
                super(itemView);
                // txtStatus = itemView.findViewById(R.id.all_users_status);
                txtUsername = itemView.findViewById(R.id.all_users_username);
                imageViewProfile = itemView.findViewById(R.id.all_users_profile_image);
                holderView = itemView.findViewById(R.id.one_item);
            }
        }
    }
}
package com.example.space.chatapp.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.SharedPreferenceHelper;
import com.example.space.chatapp.models.ProfileItem;
import com.example.space.chatapp.models.User;
import com.example.space.chatapp.ui.activities.LoginActivity;
import com.example.space.chatapp.utils.Constants;
import com.example.space.chatapp.utils.ImageUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1994; //used when activity result
    private List<ProfileItem> profileItemList = new ArrayList<>();
    private TextView tvUserName;
    private ImageView profileImage;
    private RecyclerView recyclerView;
    private ProfileInfoAdapter profileInfoAdapter;
    private LovelyProgressDialog waitingDialog;

    private DatabaseReference userDB;
    private DatabaseReference usersReference;
    private FirebaseAuth auth;
    private User myAccount;
    private Context context;
    /* related to firebase,the listener listens for data changes
   to a specific location in database and automatically provides
   the application updated data (called a snapshot)
    */
    private ValueEventListener userListener = new ValueEventListener() {

        /*This method is triggered once when the listener is attached and
        *again every time the data (including children) changes The event callback is passed
        *a snapshot containing all data at that location, including child data.
        *If there is no data, the snapshot returned is null.*/

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //get data from firebase db

            myAccount.setName(dataSnapshot.child("name").getValue(String.class));
            myAccount.setEmail(dataSnapshot.child("email").getValue(String.class));
            myAccount.setAvatar(dataSnapshot.child("avatar").getValue(String.class));
            myAccount.setBioText(dataSnapshot.child("bio").getValue(String.class));
            Log.e(MyProfileFragment.class.getName(), "************ \n my profile .name:: " + myAccount.getName());

            //put these new data in array
            fillProfileItemList(myAccount);
            Log.e(MyProfileFragment.class.getName(), "in update");

            //call adapter to put them in UI
            if (profileInfoAdapter != null) {
                profileInfoAdapter.notifyDataSetChanged();
            }

            //set user name
            if (tvUserName != null) {
                tvUserName.setText(myAccount.getName());

            }
            //set profile image
            setProfileImage(context, myAccount.getAvatar());
            //save user info into database
            SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(context);
            preferenceHelper.saveUserInfo(myAccount);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    //function to handle clicking on profileImage
    private View.OnClickListener onProfileImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(context)
                    .setTitle("Profile Image")
                    .setMessage("Are you sure want to change your profile image?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //switch to display photos
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    };

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usersReference = FirebaseDatabase.getInstance().getReference().child("user");
        usersReference.keepSynced(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        /*it will be inflated by the Android OS which basically means
        that it will be rendered by creating view object in memory.
        and it's also the process of adding a view(.xml) to activity on runtime like (setContentView)*/
        View view = inflater.inflate(R.layout.activity_profile_setting, container, false);
        context = view.getContext();
        //get profile image
        profileImage = view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(onProfileImageClick);
        //get username
        tvUserName = view.findViewById(R.id.profile_username);


        //get current user and its data to display it
        myAccount = new User();
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDB = usersReference.child(currentUid);
        Log.e(MyProfileFragment.class.getName(), "************ \n user.UID:: " + currentUid);
        //apply the event listener to this database
        userDB.addListenerForSingleValueEvent(userListener);
        auth = FirebaseAuth.getInstance();


        //get recyclerView and set adapter and manager layout for it
        recyclerView = view.findViewById(R.id.info_recycler_view);
        profileInfoAdapter = new ProfileInfoAdapter(profileItemList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(profileInfoAdapter);

        //set waiting dialog
        waitingDialog = new LovelyProgressDialog(context);
        return view;
    }

    private void fillProfileItemList(User myAccount) {

        profileItemList.clear();
        ProfileItem userNameItem = new ProfileItem(Constants.USERNAME_LABEL, myAccount.getName(), R.mipmap.ic_account_box);
        profileItemList.add(userNameItem);

        //add status
        ProfileItem bioItem = new ProfileItem(Constants.BIO_LABEL, myAccount.getBioText(), R.mipmap.ic_account_box);
        profileItemList.add(bioItem);

        ProfileItem emailItem = new ProfileItem(Constants.EMAIL_LABEL, myAccount.getEmail(), R.mipmap.ic_email);
        profileItemList.add(emailItem);

        ProfileItem resetPass = new ProfileItem(Constants.RESETPASS_LABEL, "", R.mipmap.ic_restore);
        profileItemList.add(resetPass);

        ProfileItem signout = new ProfileItem(Constants.SIGNOUT_LABEL, "", R.mipmap.ic_power_settings);
        profileItemList.add(signout);
    }

    private void setProfileImage(Context context, String img) {
        //decode from string ->bitmap->drawable
        try {
            Resources res = getResources();
            Bitmap src;
            if (img.equals("default")) {
                src = BitmapFactory.decodeResource(res, R.drawable.default_avatar);
            } else {
                byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            Drawable d = new BitmapDrawable(getResources(), src);


            profileImage.setImageDrawable(d);
            myAccount.setAvatar(img);
            SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(context);
            preferenceHelper.saveUserInfo(myAccount);
        } catch (Exception e) {
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /*
* the Adapter is a bridge between the UI components and
* the data source that fill data into the UI Component*/

    //When the user is done with the subsequent activity and returns, the system calls it
    //here we use it in case of changing profile
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(context, "An error occurred, please try again", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                //need to make image small
                imgBitmap = ImageUtil.cropImage(imgBitmap);
                //convert it to input stream
                InputStream is = ImageUtil.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtil.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtil.AVATAR_WIDTH, ImageUtil.AVATAR_HEIGHT);
                //encode it
                String imageBase64 = ImageUtil.encodeBase64(liteImage);
                myAccount.setAvatar(imageBase64);
                SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(context);
                preferenceHelper.saveUserInfo(myAccount);


                waitingDialog.setCancelable(false)
                        .setTitle("Profile image updating....")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();

                userDB.child("avatar").setValue(imageBase64)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    waitingDialog.dismiss();

                                    SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(context);
                                    preferenceHelper.saveUserInfo(myAccount);
                                    Drawable d = new BitmapDrawable(getResources(), liteImage);
                                    // draw the new images
                                    profileImage.setImageDrawable(d);

                                    new LovelyInfoDialog(context)
                                            .setTopColorRes(R.color.colorPrimary)
                                            .setTitle("Success")
                                            .setMessage("Update profile image successfully!")
                                            .show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Log.d("Update profile image ", "failed");
                                new LovelyInfoDialog(context)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setTitle("False")
                                        .setMessage("False to update profile image ")
                                        .show();
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    private void logout() {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(getActivity(), "Successfully logged out!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        } else {
                            Toast.makeText(getActivity(), "No user logged in yet!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public class ProfileInfoAdapter extends RecyclerView.Adapter<ProfileInfoAdapter.ViewHolder> {

        private List<ProfileItem> profileItemList;

        //Constructor
        public ProfileInfoAdapter(List<ProfileItem> profileItemList) {
            this.profileItemList = profileItemList;

        }

        //A ViewHolder describes an item view and metadata about its place within the RecyclerView.
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.profile_info_item, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final ProfileItem profileItem = profileItemList.get(position);
            holder.label.setText(profileItem.getLabel());
            holder.value.setText(profileItem.getValue());
            holder.icon.setImageResource(profileItem.getIcon());
            ((RelativeLayout) holder.label.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //add action for each item
                    if (profileItem.getLabel().equals(Constants.SIGNOUT_LABEL)) {
                        logout();
                    }

                    if (profileItem.getLabel().equals(Constants.USERNAME_LABEL)) {
                        //make view from edit user dialog
                        View viewInflater = LayoutInflater.from(context)
                                .inflate(R.layout.dialog_edit_username, (ViewGroup) getView(), false);
                        final EditText input = viewInflater.findViewById(R.id.edit_username);
                        input.setText(myAccount.getName());
                        //pop edit username dialog
                        new AlertDialog.Builder(context)
                                .setTitle("Edit username")
                                .setView(viewInflater)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newName = input.getText().toString();
                                        if (!myAccount.getName().equals(newName)) {
                                            changeUserName(newName);
                                        }
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                    //status
                    if (profileItem.getLabel().equals(Constants.BIO_LABEL)) {
                        //make view from edit user dialog
                        View viewInflater = LayoutInflater.from(context)
                                .inflate(R.layout.dialog_edit_userbio, (ViewGroup) getView(), false);
                        final EditText input = viewInflater.findViewById(R.id.edit_user_bio);
                        input.setText(myAccount.getBioText());
                        //pop edit username dialog
                        new AlertDialog.Builder(context)
                                .setTitle("Edit Status")
                                .setView(viewInflater)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newBio = input.getText().toString();
                                        changeUserBio(newBio);
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }


                    if (profileItem.getLabel().equals(Constants.RESETPASS_LABEL)) {
                        new AlertDialog.Builder(context)
                                .setTitle("Password")
                                .setMessage("Are you sure want to reset password?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resetPassword(myAccount.getEmail());
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                }
            });

        }


        @Override
        public int getItemCount() {
            return profileItemList.size();
        }

        private void changeUserName(String newName) {
            userDB.child("name").setValue(newName);
            myAccount.setName(newName);
            SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(context);
            prefHelper.saveUserInfo(myAccount);
            // add the listener to upload data again
            userDB.addListenerForSingleValueEvent(userListener);
        }

        private void changeUserBio(String newBio) {
            userDB.child("bio").setValue(newBio);
            myAccount.setName(newBio);
            SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(context);
            prefHelper.saveUserInfo(myAccount);
            // add the listener to upload data again
            userDB.addListenerForSingleValueEvent(userListener);
        }




        private void resetPassword(String userEmail) {

        }

        // to get elements from item.xml
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView label, value;
            public ImageView icon;

            public ViewHolder(View view) {
                super(view);
                label = view.findViewById(R.id.tv_title);
                value = view.findViewById(R.id.tv_detail);
                icon = view.findViewById(R.id.img_icon);
            }
        }
    }
}
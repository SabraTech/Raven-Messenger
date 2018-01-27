package com.example.space.chatapp.ui.activities;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.SharedPreferenceHelper;
import com.example.space.chatapp.data.StaticConfig;
import com.example.space.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity2 extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    private final Pattern VALID_EMAIL_ADDRESS = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    FloatingActionButton floatingButton;
    private EditText email, password;
    private LovelyProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_2);
        floatingButton = findViewById(R.id.floating_button);
        email = findViewById(R.id.edit_text_email_id);
        password = findViewById(R.id.edit_text_password);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new LovelyProgressDialog(this).setCancelable(false);
    }

    public void clickRegisterLayout(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, floatingButton, floatingButton.getTransitionName());
            startActivityForResult(new Intent(this, SignUpActivity2.class), StaticConfig.REQUEST_CODE_REGISTER, options.toBundle());
        } else {
            startActivityForResult(new Intent(this, SignUpActivity2.class), StaticConfig.REQUEST_CODE_REGISTER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticConfig.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            createUser(data.getStringExtra(StaticConfig.STR_EXTRA_USERNAME), data.getStringExtra(StaticConfig.STR_EXTRA_PASSWORD), data.getStringExtra(StaticConfig.STR_EXTRA_NAME));
        }
    }

    public void clickLogin(View view) {
        String username = email.getText().toString();
        String passwordString = password.getText().toString();
        if (validate(username, passwordString)) {
            signIn(username, passwordString);
        } else {
            Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void clickResetPassword(View view) {
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }

    private boolean validate(String emailString, String passwordString) {
        Matcher matcher = VALID_EMAIL_ADDRESS.matcher(emailString);
        // try to add regex for the password as well
        return (passwordString.length() > 0 || passwordString.equals(";")) && matcher.find();
    }

    private void createUser(String emailString, String passwordString, final String nameString) {
        progressDialog.setIcon(R.drawable.ic_add_friend)
                .setTitle("Registering...")
                .setTopColor(getResources().getColor(R.color.colorPrimary))
                .show();
        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(LoginActivity2.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "performFirebaseRegistration:onComplete:" + task.isSuccessful());
                        progressDialog.dismiss();

                        if (!task.isSuccessful()) {
                            new LovelyInfoDialog(LoginActivity2.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }.setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_add_friend)
                                    .setTitle("Register false")
                                    .setMessage("Email exist or weak password!")
                                    .setConfirmButtonText("ok")
                                    .setCancelable(false)
                                    .show();
                        } else {
                            initNewUserInfo(task.getResult().getUser(), nameString);
                            Toast.makeText(LoginActivity2.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity2.this, TabsActivity.class));
                            LoginActivity2.this.finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void signIn(String emailString, String passwordString) {
        progressDialog.setIcon(R.drawable.ic_person_low)
                .setTitle("Login...")
                .setTopColor(getResources().getColor(R.color.colorPrimary))
                .show();

        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(LoginActivity2.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            new LovelyInfoDialog(LoginActivity2.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_person_low)
                                    .setTitle("Login false")
                                    .setMessage("Email not exist or wrong password!")
                                    .setCancelable(false)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        } else {
                            saveUserInfo();
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity2.this, TabsActivity.class));
                            LoginActivity2.this.finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void saveUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                User userInfo = new User();
                userInfo.setName((String) hashUser.get("name"));
                userInfo.setEmail((String) hashUser.get("email"));
                userInfo.setAvatar((String) hashUser.get("avatar"));
                SharedPreferenceHelper.getInstance(LoginActivity2.this).saveUserInfo(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initNewUserInfo(FirebaseUser user, String name) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(name);
        newUser.setAvatar(StaticConfig.STR_DEFAULT_BASE64);
        FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid()).setValue(newUser);
    }
}

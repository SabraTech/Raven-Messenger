package com.example.space.chatapp.ui.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.ui.activities.ResetPasswordActivity;
import com.example.space.chatapp.ui.activities.SignupActivity;
import com.example.space.chatapp.ui.activities.UserListingActivity;
import com.example.space.chatapp.utils.Constants;
import com.example.space.chatapp.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {

    private Button loginButton, registerButton, resetButton;
    private EditText email, password;
    private String emailString, passwordString;
    private ProgressDialog progressDialog;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = fragmentView.findViewById(R.id.button_login);
        registerButton = fragmentView.findViewById(R.id.button_register);
        resetButton = fragmentView.findViewById(R.id.resetPassword);
        email = fragmentView.findViewById(R.id.edit_text_email_id);
        password = fragmentView.findViewById(R.id.edit_text_password);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // presenter

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ResetPasswordActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.startActivity(getActivity());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailString = email.getText().toString();
                passwordString = password.getText().toString();

                if (TextUtils.isEmpty(emailString)) {
                    email.setError("Enter email address!");
                    return;
                }

                if (TextUtils.isEmpty(passwordString)) {
                    password.setError("Enter password!");
                    return;
                }

                progressDialog.show();

                // authenticate user
                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                                    UserListingActivity.startActivity(getActivity(),
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    updateFirebaseToken(task.getResult().getUser().getUid(), new SharedPrefUtil(getActivity().getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(uid)
                .child(Constants.ARG_FIREBASE_TOKEN)
                .setValue(token);
    }
}

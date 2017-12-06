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
import com.example.space.chatapp.models.User;
import com.example.space.chatapp.ui.activities.UserListingActivity;
import com.example.space.chatapp.utils.Constants;
import com.example.space.chatapp.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupFragment extends Fragment {

    private static final String TAG = SignupFragment.class.getSimpleName();

    private EditText email, password, passwordCheck;
    private Button signUp;
    private ProgressDialog progressDialog;

    public static SignupFragment newInstance() {
        Bundle args = new Bundle();
        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_signup, container, false);

        signUp = fragmentView.findViewById(R.id.sign_up_button);
        email = fragmentView.findViewById(R.id.edit_text_email_id);
        password = fragmentView.findViewById(R.id.edit_text_password);
        passwordCheck = fragmentView.findViewById(R.id.edit_text_password_check);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString((R.string.please_wait)));
        progressDialog.setIndeterminate(true);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString = email.getText().toString().trim();
                String passwordString = password.getText().toString().trim();
                String passwordCheckString = passwordCheck.getText().toString().trim();

                if (TextUtils.isEmpty(emailString)) {
                    email.setError("Enter email address!");
                    return;
                }

                if (TextUtils.isEmpty(passwordString)) {
                    password.setError("Enter password!");
                    return;
                }

                if (TextUtils.isEmpty(passwordCheckString)) {
                    passwordCheck.setError("Enter password again!");
                    return;
                }

                if (passwordString.length() < 6) {
                    Toast.makeText(getActivity(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordCheckString.equals(passwordString)) {
                    Toast.makeText(getActivity(), "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.e(TAG, "performFirebaseRegistration:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    progressDialog.setMessage(getString(R.string.please_wait));
                                    Log.e(TAG, "onRegistrationFailure: " + task.getException().getMessage());
                                    Toast.makeText(getActivity(), "Registration failed!+\n" + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                } else {


                                    progressDialog.setMessage(getString(R.string.adding_user_to_db));
                                    Toast.makeText(getActivity(), "Registration Completed Successfully!", Toast.LENGTH_SHORT).show();

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    User user = new User(task.getResult().getUser().getUid(),
                                            task.getResult().getUser().getEmail(),
                                            new SharedPrefUtil(getActivity().getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN));
                                    databaseReference.child(Constants.ARG_USERS)
                                            .child(task.getResult().getUser().getUid())
                                            .setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), R.string.user_successfully_added, Toast.LENGTH_SHORT).show();
                                                        UserListingActivity.startActivity(getActivity(), Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), R.string.user_unable_to_add, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            }
                        });
            }
        });
    }


}

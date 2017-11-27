package com.example.space.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    private EditText email, password, passwordCheck;
    private Button signUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        signUp = findViewById(R.id.sign_up_button);
//        back = findViewById(R.id.btn_back);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordCheck = findViewById(R.id.passwordCheck);
        progressBar = findViewById(R.id.progressBar);

//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString().trim();
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
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordCheckString.equals(passwordString)) {
                    Toast.makeText(getApplicationContext(), "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(Signup.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Signup.this, "Registration Completed Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Signup.this, Login.class));
                            finish();
                        }
                    }
                });
            }
        });
    }
}

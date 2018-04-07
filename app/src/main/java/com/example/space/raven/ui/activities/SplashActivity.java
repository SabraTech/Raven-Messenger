package com.example.space.raven.ui.activities;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.space.raven.R;
import com.example.space.raven.data.StaticConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_MS = 500;
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private Handler mHandler;
    private Runnable mRunnable;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasReadStoragePermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasCameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
            int hasWriteStoragePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if ((hasCameraPermission != PackageManager.PERMISSION_GRANTED) && (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) && (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
            } else if (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            } else if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                displaySplashScreen();
            }
        } else {
            displaySplashScreen();
        }
    }

    private void displaySplashScreen() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                // check if user in already logged in or not
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    StaticConfig.UID = user.getUid();
                    Intent intent = new Intent(SplashActivity.this, TabsActivity.class);
                    intent.putExtra("selected_index", "0");
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }
                finish();
            }
        };
        mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED && grantResults[2] == PackageManager.PERMISSION_DENIED) {
                    // Permission Denied
                    Toast.makeText(SplashActivity.this, "Permission Denied: you will not be able to use camera and gallery!", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (permissions[0].equals(android.Manifest.permission.CAMERA)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(SplashActivity.this, "Permission Denied: you will not be able to use camera!", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else if (permissions[0].equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(SplashActivity.this, "Permission Denied: you will not be able to view the gallery!", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else if (permissions[0].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(SplashActivity.this, "Permission Denied: app will not be able to temporary save the captured image!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        displaySplashScreen();
    }

}

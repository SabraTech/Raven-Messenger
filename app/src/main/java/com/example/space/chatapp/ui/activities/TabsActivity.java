package com.example.space.chatapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.ui.adapters.TabsPageAdapter;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by gehad on 26/01/18.
 */

public class TabsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsPageAdapter tabsPageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);


        viewPager = findViewById(R.id.main_tabs_pager);
        tabsPageAdapter = new TabsPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPageAdapter);
        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_profile) {
            Intent profileIntent = new Intent(
                    TabsActivity.this, ProfileSettingActivity.class);
            startActivity(profileIntent);

        }
        if (item.getItemId() == R.id.menu_all_users) {
            Intent profileIntent = new Intent(
                    TabsActivity.this, UserListingActivity.class);
            startActivity(profileIntent);

        }
        if (item.getItemId() == R.id.action_logout) {
            logout();
        }

        return true;
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(TabsActivity.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(TabsActivity.this, LoginActivity2.class));
                        } else {
                            Toast.makeText(TabsActivity.this, "No user logged in yet!", Toast.LENGTH_SHORT).show();
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
}
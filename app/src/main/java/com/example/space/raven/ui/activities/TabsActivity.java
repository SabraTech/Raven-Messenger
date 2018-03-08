package com.example.space.raven.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.space.raven.R;
import com.example.space.raven.ui.adapters.TabsPageAdapter;
import com.example.space.raven.ui.fragments.FriendsFragment;
import com.example.space.raven.ui.fragments.MyProfileFragment;
import com.example.space.raven.ui.fragments.NotificationFragment;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Class to handle tabs and upper pop up menu
 */

public class TabsActivity extends AppCompatActivity {

    public static String STR_CHAT_FRAGMENT = "CHATS";
    public static String STR_GROUP_FRAGMENT = "GROUPS";
    public static String STR_NOTIF_FRAGMENT = "NOTIFICATION";
    public static String STR_PROFILE_FRAGMENT = "PROFILE";
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsPageAdapter tabsPageAdapter;
    private FloatingActionButton floatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        viewPager = findViewById(R.id.viewpager);
        floatingButton = findViewById(R.id.fab);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TabsActivity.this, AllUsersActivity.class));
            }
        });
        initTab();
        int index = 0;
        if (getIntent().getStringExtra("selected_index") != null) {
            // select the tab to open on
            index = Integer.parseInt(getIntent().getStringExtra("selected_index"));
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            tab.select();
        } else {
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            tab.select();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasReadStoragePermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasCameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);

            if ((hasCameraPermission != PackageManager.PERMISSION_GRANTED) && (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
            } else if (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void initTab() {
        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_tab_person,
                // R.drawable.ic_tab_group,
                R.drawable.ic_tab_notif,
                R.drawable.ic_tab_infor
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        // tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager pager) {
        tabsPageAdapter = new TabsPageAdapter(getSupportFragmentManager());
        tabsPageAdapter.addFrag(new FriendsFragment(), STR_CHAT_FRAGMENT);
        // tabsPageAdapter.addFrag(new GroupsFragment(), STR_GROUP_FRAGMENT);
        tabsPageAdapter.addFrag(new NotificationFragment(), STR_NOTIF_FRAGMENT);
        tabsPageAdapter.addFrag(new MyProfileFragment(), STR_PROFILE_FRAGMENT);

        // set the onClick for the floating button with the friendsFragment one

        pager.setAdapter(tabsPageAdapter);
        pager.setOffscreenPageLimit(3);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (tabsPageAdapter.getItem(position) instanceof FriendsFragment) {
                    setActionBarTitle(STR_CHAT_FRAGMENT);
                    floatingButton.setVisibility(View.VISIBLE);
                    floatingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(TabsActivity.this, AllUsersActivity.class));
                        }
                    });
                    floatingButton.setImageResource(R.drawable.plus);
                } else if (tabsPageAdapter.getItem(position) instanceof NotificationFragment) {
                    setActionBarTitle(STR_NOTIF_FRAGMENT);
                    floatingButton.setVisibility(View.GONE);
                } else {
                    setActionBarTitle(STR_PROFILE_FRAGMENT);
                    floatingButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * handle upper pop menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_all_users) {
            Intent allUsersIntent = new Intent(
                    TabsActivity.this, AllUsersActivity.class);
            startActivity(allUsersIntent);

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
                            startActivity(new Intent(TabsActivity.this, LoginActivity.class));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    // Permission Denied
                    Toast.makeText(TabsActivity.this, "Permission Denied: you will not be able to use camera and gallery!", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (permissions[0].equals(android.Manifest.permission.CAMERA)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(TabsActivity.this, "Permission Denied: you will not be able to use camera!", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else if (permissions[0].equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(TabsActivity.this, "Permission Denied: you will not be able to view the gallery!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
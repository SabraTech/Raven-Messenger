package com.example.space.chatapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.space.chatapp.R;
import com.example.space.chatapp.ui.adapters.TabsPageAdapter;

/**
 * Created by gehad on 26/01/18.
 */

public class TabsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsPageAdapter tabsPageAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);


        viewPager = (ViewPager)findViewById(R.id.main_tabs_pager);
        tabsPageAdapter = new TabsPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPageAdapter);
        tabLayout=(TabLayout)findViewById(R.id.main_tabs);
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
        if (item.getItemId()==R.id.menu_profile){
            Intent profileIntent = new Intent(
                    TabsActivity.this,ProfileSettingActivity.class);
            startActivity(profileIntent);

        }
        if (item.getItemId()==R.id.menu_all_users){
            Intent profileIntent = new Intent(
                    TabsActivity.this,ProfileSettingActivity.class);
            startActivity(profileIntent);

        }

        return true ;
    }
}

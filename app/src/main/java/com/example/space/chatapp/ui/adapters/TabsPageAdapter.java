package com.example.space.chatapp.ui.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.space.chatapp.ui.fragments.GroupsFragment;
import com.example.space.chatapp.ui.fragments.MyProfileFragment;
import com.example.space.chatapp.ui.fragments.RequestsFragment;

/**
 * the Adapter is a bridge between the UI components and
 * the data source that fill data into the UI Component
 */

public class TabsPageAdapter extends FragmentPagerAdapter {
    public TabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            case 1:
//                ChatFragment chatFragment=new ChatFragment();
//                return chatFragment;

            case 2:
                return new GroupsFragment();

            case 3:
                MyProfileFragment myProfileFragment = new MyProfileFragment();
                return myProfileFragment;

            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "notif";

            case 1:
                return "Chat";

            case 2:
                return "Groups";

            case 3:
                return "Profile";

            default:
                return null;

        }


    }
}

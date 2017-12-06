package com.example.space.chatapp.ui.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.space.chatapp.ui.fragments.UsersFragment;

public class UserListingPagerAdapter extends FragmentPagerAdapter {

    private static final Fragment[] fragments = new Fragment[]{
            UsersFragment.newInstance(UsersFragment.TYPE_ALL)
    };

    private static final String[] titles = new String[]{
            "All Users"
    };

    public UserListingPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

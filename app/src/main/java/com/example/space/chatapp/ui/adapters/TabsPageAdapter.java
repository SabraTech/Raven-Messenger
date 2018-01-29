package com.example.space.chatapp.ui.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * the Adapter is a bridge between the UI components and
 * the data source that fill data into the UI Component
 */

public class TabsPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabsPageAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
//        switch (position) {
//            case 0:
//                RequestsFragment requestsFragment = new RequestsFragment();
//                return requestsFragment;
//
//            case 1:
////                ChatFragment chatFragment=new ChatFragment();
////                return chatFragment;
//
//            case 2:
//                return new GroupsFragment();
//
//            case 3:
//                MyProfileFragment myProfileFragment = new MyProfileFragment();
//                return myProfileFragment;
//
//            default:
//                return null;

    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        fragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // return null to display only the icon
        return null;
    }
}

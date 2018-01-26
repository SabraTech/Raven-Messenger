package com.example.space.chatapp.ui.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.space.chatapp.ui.fragments.ChatFragment;
import com.example.space.chatapp.ui.fragments.ChatTempFragment;
import com.example.space.chatapp.ui.fragments.FriendsFragment;
import com.example.space.chatapp.ui.fragments.RequestsFragment;

public class TabsPageAdapter extends FragmentPagerAdapter{
    public TabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestsFragment requestsFragment=new RequestsFragment();
                return requestsFragment;

            case 1 :
                ChatTempFragment chatFragment=new ChatTempFragment();
                return chatFragment;

            case 2 :
                FriendsFragment friendsFragment= new FriendsFragment();
                return friendsFragment;

            default :
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle (int position){
        switch (position){
            case 0:
                return "Requests";

            case 1 :
                return "Chat";

            case 2 :
                return "Friends";

            default :
                return null;

        }


    }
}

package com.example.mywhatsappclone;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessAdapter extends FragmentPagerAdapter {
    public TabAccessAdapter(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                ChatyFragment chatyFragment = new ChatyFragment();
                return chatyFragment;
            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 2:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            default:
                return null;


        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:

                return "Contacts";
            case 2:

                return "Groups";
            default:
                return null;


        }

    }
}

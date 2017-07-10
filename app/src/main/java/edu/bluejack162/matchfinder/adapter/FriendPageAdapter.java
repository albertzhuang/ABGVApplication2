package edu.bluejack162.matchfinder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alber on 10/07/2017.
 */

public class FriendPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments = new ArrayList<Fragment>();
    private final List<String> titles = new ArrayList<String>();

    public FriendPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}

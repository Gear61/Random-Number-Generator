package com.randomappsinc.randomnumbergeneratorplus.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.randomnumbergeneratorplus.Fragments.RNGFragment;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;

/**
 * Created by alexanderchiou on 1/1/17.
 */

public class HomepageTabsAdapter extends FragmentStatePagerAdapter {
    private String[] tabNames;

    public HomepageTabsAdapter (FragmentManager fragmentManager) {
        super(fragmentManager);
        tabNames = MyApplication.getAppContext().getResources().getStringArray(R.array.homepage_options);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RNGFragment();
            case 1:
                return new RNGFragment();
            default:
                return new RNGFragment();
        }
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }
}

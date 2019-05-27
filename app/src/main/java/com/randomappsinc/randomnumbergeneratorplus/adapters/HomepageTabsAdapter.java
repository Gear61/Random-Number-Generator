package com.randomappsinc.randomnumbergeneratorplus.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.randomappsinc.randomnumbergeneratorplus.fragments.CoinsFragment;
import com.randomappsinc.randomnumbergeneratorplus.fragments.DiceFragment;
import com.randomappsinc.randomnumbergeneratorplus.fragments.LottoFragment;
import com.randomappsinc.randomnumbergeneratorplus.fragments.RNGFragment;

public class HomepageTabsAdapter extends FragmentStatePagerAdapter {

    private String[] tabNames;
    private RNGFragment rngFragment;
    private DiceFragment diceFragment;
    private LottoFragment lottoFragment;
    private CoinsFragment coinsFragment;

    public HomepageTabsAdapter(FragmentManager fragmentManager, String[] tabNames) {
        super(fragmentManager);
        this.tabNames = tabNames;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (rngFragment == null) {
                    rngFragment = new RNGFragment();
                }
                return rngFragment;
            case 1:
                if (diceFragment == null) {
                    diceFragment = new DiceFragment();
                }
                return diceFragment;
            case 2:
                if (lottoFragment == null) {
                    lottoFragment = new LottoFragment();
                }
                return lottoFragment;
            case 3:
                if (coinsFragment == null) {
                    coinsFragment = new CoinsFragment();
                }
                return coinsFragment;
            default:
                return null;
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

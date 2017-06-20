package com.randomappsinc.randomnumbergeneratorplus.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.randomnumbergeneratorplus.Fragments.CoinsFragment;
import com.randomappsinc.randomnumbergeneratorplus.Fragments.DiceFragment;
import com.randomappsinc.randomnumbergeneratorplus.Fragments.LottoFragment;
import com.randomappsinc.randomnumbergeneratorplus.Fragments.RNGFragment;
import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.Utils.MyApplication;

/**
 * Created by alexanderchiou on 1/1/17.
 */

public class HomepageTabsAdapter extends FragmentStatePagerAdapter {
    private String[] tabNames;
    private RNGFragment rngFragment;
    private DiceFragment diceFragment;
    private LottoFragment lottoFragment;
    private CoinsFragment coinsFragment;

    public HomepageTabsAdapter (FragmentManager fragmentManager) {
        super(fragmentManager);
        tabNames = MyApplication.getAppContext().getResources().getStringArray(R.array.homepage_options);
    }

    public void generate(int position) {
        switch (position) {
            case 0:
                rngFragment.generate();
                break;
            case 1:
                diceFragment.roll();
                break;
            case 2:
                lottoFragment.generateTickets();
                break;
            case 3:
                coinsFragment.flip();
                break;
        }
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

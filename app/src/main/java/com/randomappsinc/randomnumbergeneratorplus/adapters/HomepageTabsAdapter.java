package com.randomappsinc.randomnumbergeneratorplus.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.randomappsinc.randomnumbergeneratorplus.R;
import com.randomappsinc.randomnumbergeneratorplus.fragments.CoinsFragment;
import com.randomappsinc.randomnumbergeneratorplus.fragments.DiceFragment;
import com.randomappsinc.randomnumbergeneratorplus.fragments.LottoFragment;
import com.randomappsinc.randomnumbergeneratorplus.fragments.RNGFragment;
import com.randomappsinc.randomnumbergeneratorplus.utils.MyApplication;

public class HomepageTabsAdapter extends FragmentStatePagerAdapter {

    private String[] tabNames;
    private RNGFragment rngFragment;
    private DiceFragment diceFragment;
    private LottoFragment lottoFragment;
    private CoinsFragment coinsFragment;

    public HomepageTabsAdapter(FragmentManager fragmentManager, Bundle bundle) {
        super(fragmentManager);
        tabNames = MyApplication.getAppContext().getResources().getStringArray(R.array.homepage_options);

        if (bundle != null) {
            this.rngFragment = (RNGFragment) fragmentManager.getFragment(bundle, RNGFragment.TAG);
            this.diceFragment = (DiceFragment) fragmentManager.getFragment(bundle, DiceFragment.TAG);
            this.lottoFragment = (LottoFragment) fragmentManager.getFragment(bundle, LottoFragment.TAG);
            this.coinsFragment = (CoinsFragment) fragmentManager.getFragment(bundle, CoinsFragment.TAG);
        }
    }

    public RNGFragment getRngFragment() {
        return rngFragment;
    }

    public DiceFragment getDiceFragment() {
        return diceFragment;
    }

    public LottoFragment getLottoFragment() {
        return lottoFragment;
    }

    public CoinsFragment getCoinsFragment() {
        return coinsFragment;
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

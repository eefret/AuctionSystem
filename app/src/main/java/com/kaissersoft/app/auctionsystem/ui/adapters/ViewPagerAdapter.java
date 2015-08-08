package com.kaissersoft.app.auctionsystem.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kaissersoft.app.auctionsystem.activities.fragments.AuctionListFragment;

/**
 * Created by eefret on 08/04/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence mTitles[];
    private int numbOfTabs;
    private long userID;

    public ViewPagerAdapter(FragmentManager fm,
                            CharSequence titles[],
                            int tabCount,
                            long userID) {
        super(fm);
        this.mTitles = titles;
        this.numbOfTabs = tabCount;
        this.userID = userID;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return AuctionListFragment.newInstance(userID, AuctionListFragment.ListType.ALL);
            case 1:
                return AuctionListFragment.newInstance(userID, AuctionListFragment.ListType.BIDDING);
            case 2:
                return AuctionListFragment.newInstance(userID, AuctionListFragment.ListType.WON);
            default:
                return AuctionListFragment.newInstance(userID, AuctionListFragment.ListType.ALL);
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }
}

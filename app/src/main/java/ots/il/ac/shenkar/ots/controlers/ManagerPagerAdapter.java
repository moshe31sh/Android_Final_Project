package ots.il.ac.shenkar.ots.controlers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ots.il.ac.shenkar.ots.fragment.AllTaskFragment;
import ots.il.ac.shenkar.ots.fragment.DoneTaskFragment;
import ots.il.ac.shenkar.ots.fragment.InProcessTaskFragment;
import ots.il.ac.shenkar.ots.fragment.RejectedTaskFragment;
import ots.il.ac.shenkar.ots.fragment.WaitingTaskFragment;

/**
 * Created by moshe on 21-02-16.
 */
public class ManagerPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;


    public ManagerPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;

    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsEmployee" , false);
        switch (position) {
            case 0:
                // set Fragmentclass Arguments
                WaitingTaskFragment tab1 = new WaitingTaskFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                InProcessTaskFragment tab2 = new InProcessTaskFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                DoneTaskFragment tab3 = new DoneTaskFragment();
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                RejectedTaskFragment tab4 = new RejectedTaskFragment();
                tab4.setArguments(bundle);
                return tab4;
            case 4:
                AllTaskFragment tab5 = new AllTaskFragment();
                tab5.setArguments(bundle);
                return tab5;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
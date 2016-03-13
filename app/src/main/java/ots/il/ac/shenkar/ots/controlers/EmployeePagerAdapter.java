package ots.il.ac.shenkar.ots.controlers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ots.il.ac.shenkar.ots.fragment.AllTaskFragment;
import ots.il.ac.shenkar.ots.fragment.DoneTaskFragment;
import ots.il.ac.shenkar.ots.fragment.InProcessTaskFragment;
import ots.il.ac.shenkar.ots.fragment.WaitingTaskFragment;

/**
 * Created by moshe on 22-02-16.
 */
public class EmployeePagerAdapter  extends FragmentStatePagerAdapter {

    private int mNumOfTabs;


    public EmployeePagerAdapter(FragmentManager fm, int numOfTabs, String employeeName) {
        super(fm);
        this.mNumOfTabs = numOfTabs;

    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsEmployee" , true);
        switch (position) {
            case 0:
                // set Fragmentclass Arguments
                WaitingTaskFragment tab1 = new WaitingTaskFragment();
                bundle.putString("TaskStatus", "Waiting");
                tab1.setArguments(bundle);

                return tab1;
            case 1:
                InProcessTaskFragment tab2 = new InProcessTaskFragment();
                bundle.putString("TaskStatus", "In progress");
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                DoneTaskFragment tab3 = new DoneTaskFragment();
                bundle.putString("TaskStatus", "Done");
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                // set Fragmentclass Arguments
                AllTaskFragment tab4 = new AllTaskFragment();
                bundle.putString("TaskStatus", "All");
                tab4.setArguments(bundle);

                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
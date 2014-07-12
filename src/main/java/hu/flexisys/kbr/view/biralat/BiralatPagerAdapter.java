package hu.flexisys.kbr.view.biralat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralatPagerAdapter extends FragmentPagerAdapter {

    public BiralatPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new KeresoFragment();
                break;
            case 1:
                fragment = new BiralFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

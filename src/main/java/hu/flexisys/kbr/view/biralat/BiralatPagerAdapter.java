package hu.flexisys.kbr.view.biralat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import hu.flexisys.kbr.view.biralat.biral.BiralFragment;
import hu.flexisys.kbr.view.biralat.kereso.KeresoFragment;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralatPagerAdapter extends FragmentPagerAdapter {

    private KeresoFragment keresoFragment;
    private BiralFragment biralFragment;

    public BiralatPagerAdapter(FragmentManager fm, BiralatActivity biralatActivity) {
        super(fm);
        keresoFragment = KeresoFragment.newInstance(biralatActivity);
        biralFragment = BiralFragment.newInstance(biralatActivity);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = keresoFragment;
                break;
            case 1:
                fragment = biralFragment;
                break;
            default:
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public KeresoFragment getKeresoFragment() {
        return keresoFragment;
    }

    public BiralFragment getBiralFragment() {
        return biralFragment;
    }

}

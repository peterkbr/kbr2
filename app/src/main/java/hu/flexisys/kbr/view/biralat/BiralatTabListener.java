package hu.flexisys.kbr.view.biralat;

import android.support.design.widget.TabLayout;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralatTabListener implements TabLayout.OnTabSelectedListener {

    private BiralatActivity activity;

    public BiralatTabListener(BiralatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            activity.onExit();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}

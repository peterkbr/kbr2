package hu.flexisys.kbr.view.biralat;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralatTabListener implements TabLayout.OnTabSelectedListener {

    private ViewPager pager;
    private BiralatActivity activity;
    private boolean started = false;

    public BiralatTabListener(BiralatActivity activity, ViewPager pager) {
        this.activity = activity;
        this.pager = pager;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int index = tab.getPosition();
        if (!started) {
            pager.setCurrentItem(0);
            started = true;
            return;
        }
        if (index == 0) {
            activity.onExit();
        } else {
            pager.setCurrentItem(--index);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}

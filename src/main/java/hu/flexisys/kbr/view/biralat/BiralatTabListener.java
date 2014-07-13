package hu.flexisys.kbr.view.biralat;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralatTabListener implements ActionBar.TabListener {

    private ViewPager pager;
    private Activity activity;
    private boolean started = false;

    public BiralatTabListener(Activity activity, ViewPager pager) {
        this.activity = activity;
        this.pager = pager;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        int index = tab.getPosition();
        if (!started) {
            pager.setCurrentItem(0);
            started = true;
            return;
        }
        if (index == 0) {
            activity.finish();
        } else {
            pager.setCurrentItem(--index);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // nothing to do
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // nothing to do
    }
}

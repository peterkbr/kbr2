package hu.flexisys.kbr.view.biralat;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralatPageChangeListener implements ViewPager.OnPageChangeListener {

    private ActionBar actionBar;

    public BiralatPageChangeListener(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    public void onPageSelected(int index) {
        actionBar.setSelectedNavigationItem(++index);
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // nothing to do
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // nothing to do
    }
}

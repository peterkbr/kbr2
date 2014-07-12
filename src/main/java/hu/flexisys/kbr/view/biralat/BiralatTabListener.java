package hu.flexisys.kbr.view.biralat;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralatTabListener implements ActionBar.TabListener {

    private ViewPager pager;

    public BiralatTabListener(ViewPager pager) {
        this.pager = pager;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        pager.setCurrentItem(tab.getPosition());
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

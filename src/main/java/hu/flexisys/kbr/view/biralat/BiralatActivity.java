package hu.flexisys.kbr.view.biralat;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrActivity;

/**
 * Created by Peter on 2014.07.04..
 */
public class BiralatActivity extends KbrActivity {

    private ViewPager pager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.activity_biralat);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        pager = (ViewPager) findViewById(R.id.biralat_pager);
        pager.setAdapter(new BiralatPagerAdapter(getSupportFragmentManager()));

        BiralatPageChangeListener biralatPageChangeListener = new BiralatPageChangeListener(actionBar);
        pager.setOnPageChangeListener(biralatPageChangeListener);

        BiralatTabListener biralatTabListener = new BiralatTabListener(pager);
        actionBar.addTab(actionBar.newTab().setText("Kereső").setTabListener(biralatTabListener));
        actionBar.addTab(actionBar.newTab().setText("Bírál").setTabListener(biralatTabListener));

    }

}
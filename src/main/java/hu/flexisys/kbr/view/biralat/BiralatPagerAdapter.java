package hu.flexisys.kbr.view.biralat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.view.biralat.biral.BiralFragment;
import hu.flexisys.kbr.view.biralat.kereso.KeresoFragment;

public class BiralatPagerAdapter extends FragmentPagerAdapter {

    private KeresoFragment keresoFragment;
    private BiralFragment biralFragment;

    public BiralatPagerAdapter(FragmentManager fm, final BiralatActivity biralatActivity) {
        super(fm);
        keresoFragment = KeresoFragment.newInstance(biralatActivity);
        biralFragment = BiralFragment.newInstance(new BiralFragment.BiralFragmentContainer() {
            @Override
            public void onAkako(String akako) {
                biralatActivity.onAkako(akako);
            }

            @Override
            public void onBiralFragmentResume(BiralFragment biralFragment) {
                biralatActivity.onBiralFragmentResume();
            }

            @Override
            public void selectBiralat(Biralat lastBiralat) {
                biralatActivity.selectBiralat(lastBiralat);
            }

            @Override
            public void openMegjegyzesDialog(String megjegyzes) {
                biralatActivity.openMegjegyzesDialog(megjegyzes);
            }
        });
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

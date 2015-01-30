package hu.flexisys.kbr.view.levalogatas.biralatdialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.view.KbrDialog;

import java.util.List;

/**
 * Created by peter on 04/09/14.
 */
public class BiralatDialog extends KbrDialog {

    private List<Biralat> biralatList;

    public static BiralatDialog newInstance(List<Biralat> biralatList) {
        BiralatDialog f = new BiralatDialog();
        f.biralatList = biralatList;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_biralat;
        View v = super.onCreateView(inflater, container, savedInstanceState);

        ViewPager pager = (ViewPager) v.findViewById(R.id.dialog_biralat_pager);
        BiralatDialogFragmentAdapter adapter = new BiralatDialogFragmentAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        Button ok = (Button) v.findViewById(R.id.dialog_biralat_button_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                getActivity().finish();
            }
        });
        return v;
    }

    private class BiralatDialogFragmentAdapter extends FragmentStatePagerAdapter {

        public BiralatDialogFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BiralatDialogFragment.newInstance(biralatList.get(position), position + 1, biralatList.size());
        }

        @Override
        public int getCount() {
            return biralatList.size();
        }
    }
}

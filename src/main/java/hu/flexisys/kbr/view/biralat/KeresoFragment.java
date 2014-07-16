package hu.flexisys.kbr.view.biralat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import hu.flexisys.kbr.R;

/**
 * Created by Peter on 2014.07.11..
 */
public class KeresoFragment extends Fragment {

    private BiralatActivity activity;

    public static KeresoFragment newInstance(BiralatActivity activity) {
        KeresoFragment fragment = new KeresoFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_biralat_kereso, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.onKeresoFragmentResume();
    }

}

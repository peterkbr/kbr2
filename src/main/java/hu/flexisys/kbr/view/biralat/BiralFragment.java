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
public class BiralFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_biralat_biral, container, false);
        return v;
    }
}

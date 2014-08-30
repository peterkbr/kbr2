package hu.flexisys.kbr.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hu.flexisys.kbr.R;

/**
 * Created by Peter on 2014.07.09..
 */
public class ProgressDialog extends KbrDialog {

    private String title;
    public static String BASE_TITLE = "Betöltés folyamatban...";

    public static ProgressDialog newInstance(String title) {
        ProgressDialog f = new ProgressDialog();
        f.layoutResId = R.layout.dialog_progress;
        f.setCancelable(false);
        f.title = title;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView titleView = (TextView) view.findViewById(R.id.dialog_progress_title);
        titleView.setText(title);
        return view;
    }
}

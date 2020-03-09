package hu.flexisys.kbr.view.bongeszo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by peter on 01/08/14.
 */
public class BongMegjegyzesDialog extends KbrDialog {

    private String megjegyzes;

    public static BongMegjegyzesDialog newInstance(String megjegyzes) {
        BongMegjegyzesDialog dialog = new BongMegjegyzesDialog();
        dialog.megjegyzes = megjegyzes;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_bong_megjegyzes;
        View v = super.onCreateView(inflater, container, savedInstanceState);
        final EditText megjegyzesET = (EditText) v.findViewById(R.id.bong_megjegyzes);
        megjegyzesET.setText(megjegyzes);
        return v;
    }
}

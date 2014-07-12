package hu.flexisys.kbr.view;

import hu.flexisys.kbr.R;

/**
 * Created by Peter on 2014.07.09..
 */
public class ProgressDialog extends KbrDialog {

    public static ProgressDialog newInstance() {
        ProgressDialog f = new ProgressDialog();
        f.layoutResId = R.layout.dialog_progress;
        f.setCancelable(false);
        return f;
    }
}

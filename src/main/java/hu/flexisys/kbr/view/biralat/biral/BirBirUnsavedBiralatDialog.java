package hu.flexisys.kbr.view.biralat.biral;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by peter on 01/08/14.
 */
public class BirBirUnsavedBiralatDialog extends KbrDialog {

    private BirBirUnsavedBiralatListener listener;

    public static BirBirUnsavedBiralatDialog newInstance(BirBirUnsavedBiralatListener listener) {
        BirBirUnsavedBiralatDialog dialog = new BirBirUnsavedBiralatDialog();
        dialog.layoutResId = R.layout.dialog_bir_bir_unsaved;
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button ok = (Button) v.findViewById(R.id.bir_ker_dialog_notselected_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBirBirUnsavedBiralatOk();
            }
        });

        Button cancel = (Button) v.findViewById(R.id.bir_ker_dialog_notselected_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBirBirUnsavedBiralatCancel();
            }
        });
        return v;
    }
}

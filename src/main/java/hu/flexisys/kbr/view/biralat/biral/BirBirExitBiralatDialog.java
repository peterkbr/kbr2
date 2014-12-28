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
public class BirBirExitBiralatDialog extends KbrDialog {

    private BirBirExitBiralatDialogListener listener;

    public static BirBirExitBiralatDialog newInstance(BirBirExitBiralatDialogListener listener) {
        BirBirExitBiralatDialog dialog = new BirBirExitBiralatDialog();
        dialog.layoutResId = R.layout.dialog_bir_bir_exit;
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button ok = (Button) v.findViewById(R.id.bir_bir_dialog_exit_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBirBirExitBiralatOk();
            }
        });

        Button cancel = (Button) v.findViewById(R.id.bir_bir_dialog_exit_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBirBirExitBiralatCancel();
            }
        });
        return v;
    }

    public interface BirBirExitBiralatDialogListener {

        public void onBirBirExitBiralatOk();

        public void onBirBirExitBiralatCancel();
    }
}

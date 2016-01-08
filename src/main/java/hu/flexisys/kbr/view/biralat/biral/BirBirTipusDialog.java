package hu.flexisys.kbr.view.biralat.biral;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

public class BirBirTipusDialog extends KbrDialog {

    private BirBirTipusListener listener;

    public static BirBirTipusDialog newInstance(BirBirTipusListener listener) {
        BirBirTipusDialog f = new BirBirTipusDialog();
        f.listener = listener;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_bir_bir_tipus;
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button hus = (Button) v.findViewById(R.id.bir_bir_dialog_tipus_hus);
        hus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onHus();
            }
        });

        Button tej = (Button) v.findViewById(R.id.bir_bir_dialog_tipus_tej);
        tej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTej();
            }
        });

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}

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
public class BirBirAkakoDialog extends KbrDialog {

    private BirBirAkakoDialogListener listener;


    public static BirBirAkakoDialog newInstance(BirBirAkakoDialogListener listener) {
        BirBirAkakoDialog dialog = new BirBirAkakoDialog();
        dialog.listener = listener;
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_bir_bir_akako;
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button cancel = (Button) v.findViewById(R.id.bir_bir_dialog_akako_nem);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoClicked();
            }
        });

        Button ok = (Button) v.findViewById(R.id.bir_bir_dialog_akako_igen);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onYesClicked();
            }
        });
        return v;
    }

    public interface BirBirAkakoDialogListener {
        public void onNoClicked();

        public void onYesClicked();
    }
}

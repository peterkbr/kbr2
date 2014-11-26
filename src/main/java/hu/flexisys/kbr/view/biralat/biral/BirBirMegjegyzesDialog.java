package hu.flexisys.kbr.view.biralat.biral;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by peter on 01/08/14.
 */
public class BirBirMegjegyzesDialog extends KbrDialog {

    private BirBirMegjegyzesListener listener;
    private String megjegyzes;

    public static BirBirMegjegyzesDialog newInstance(BirBirMegjegyzesListener listener, String megjegyzes) {
        BirBirMegjegyzesDialog dialog = new BirBirMegjegyzesDialog();
        dialog.layoutResId = R.layout.dialog_bir_bir_megjegyzes;
        dialog.listener = listener;
        dialog.megjegyzes = megjegyzes;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        final EditText megjegyzesET = (EditText) v.findViewById(R.id.bir_bir_megjegyzes);
        megjegyzesET.setText(megjegyzes);

        Button ok = (Button) v.findViewById(R.id.bir_bir_megjegyzes_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                megjegyzes = megjegyzesET.getText().toString();
                listener.onBirBirBirBirMegjegyzesOk(megjegyzesET, megjegyzes);
            }
        });

        Button cancel = (Button) v.findViewById(R.id.bir_bir_megjegyzes_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBirBirBirBirMegjegyzesCancel(megjegyzesET);
            }
        });
        return v;
    }

    public interface BirBirMegjegyzesListener {

        public void onBirBirBirBirMegjegyzesCancel(EditText et);

        public void onBirBirBirBirMegjegyzesOk(EditText et, String megjegyzes);
    }
}

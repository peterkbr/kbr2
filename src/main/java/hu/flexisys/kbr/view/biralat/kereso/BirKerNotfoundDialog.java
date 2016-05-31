package hu.flexisys.kbr.view.biralat.kereso;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;
import hu.flexisys.kbr.view.component.numpad.NumPad;
import hu.flexisys.kbr.view.component.numpad.NumPadInput;

import java.util.ArrayList;
import java.util.List;

public class BirKerNotfoundDialog extends KbrDialog {

    private BirKerNotfoundListener listener;
    private String[] selectedTenazArray;
    private String hasznalatiSzamValue;

    private NumPadInput azonNumPadInput;
    private Spinner tenazSpinner;
    private Spinner orskoSpinner;

    public static BirKerNotfoundDialog newInstance(BirKerNotfoundListener listener, String[] selectedTenazArray, String hasznalatiSzamValue) {
        BirKerNotfoundDialog f = new BirKerNotfoundDialog();
        f.listener = listener;
        f.selectedTenazArray = selectedTenazArray;
        f.hasznalatiSzamValue = hasznalatiSzamValue;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_bir_ker_notfound;
        View v = super.onCreateView(inflater, container, savedInstanceState);

        tenazSpinner = (Spinner) v.findViewById(R.id.bir_ker_dialog_notfound_tenaz_spinner);
        List<String> list = new ArrayList<String>();
        for (String tenaz : selectedTenazArray) {
            list.add(tenaz);
        }
        tenazSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list));

        orskoSpinner = (Spinner) v.findViewById(R.id.bir_ker_dialog_notfound_orsko_spinner);

        azonNumPadInput = (NumPadInput) v.findViewById(R.id.bir_ker_dialog_notfound_azon);
        NumPad numpad = (NumPad) v.findViewById(R.id.bir_ker_dialog_notfound_numpad);
        numpad.setNumPadInput(azonNumPadInput);
        azonNumPadInput.setText(hasznalatiSzamValue);

        Button ok = (Button) v.findViewById(R.id.bir_ker_dialog_notfound_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenaz = String.valueOf(tenazSpinner.getSelectedItem());
                String orsko = String.valueOf(orskoSpinner.getSelectedItem());
                String azon = azonNumPadInput.getText().toString();
                if (azon != null && !azon.isEmpty() && tenaz != null && !tenaz.isEmpty() && orsko != null && !orsko.isEmpty()) {
                    listener.onAddNewEgyed(tenaz, orsko, azon);
                }
            }
        });

        Button cancel = (Button) v.findViewById(R.id.bir_ker_dialog_notfound_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }
}

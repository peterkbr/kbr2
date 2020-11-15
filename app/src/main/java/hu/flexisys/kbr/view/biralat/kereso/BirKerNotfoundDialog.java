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
import java.util.Collections;
import java.util.List;

/**
 * Created by Peter on 2014.07.08..
 */
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
        if (v == null) {
            return null;
        }

        tenazSpinner = v.findViewById(R.id.bir_ker_dialog_notfound_tenaz_spinner);
        List<String> list = new ArrayList<>();
        Collections.addAll(list, selectedTenazArray);
        tenazSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list));

        orskoSpinner = v.findViewById(R.id.bir_ker_dialog_notfound_orsko_spinner);

        azonNumPadInput = v.findViewById(R.id.bir_ker_dialog_notfound_azon);
        NumPad numpad = v.findViewById(R.id.bir_ker_dialog_notfound_numpad);
        numpad.setNumPadInput(azonNumPadInput);
        azonNumPadInput.setText(hasznalatiSzamValue);

        Button ok = v.findViewById(R.id.bir_ker_dialog_notfound_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenaz = String.valueOf(tenazSpinner.getSelectedItem());
                String orsko = String.valueOf(orskoSpinner.getSelectedItem());
                String azon = azonNumPadInput.getText().toString();
                if (!azon.isEmpty() && !tenaz.isEmpty() && !orsko.isEmpty()) {
                    listener.onAddNewEgyed(tenaz, orsko, azon);
                }
            }
        });

        Button cancel = v.findViewById(R.id.bir_ker_dialog_notfound_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }
}
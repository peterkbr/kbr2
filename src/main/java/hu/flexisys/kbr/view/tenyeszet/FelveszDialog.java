package hu.flexisys.kbr.view.tenyeszet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by Peter on 2014.07.08..
 */
public class FelveszDialog extends KbrDialog {

    private FelveszListener listener;
    private EditText azonEdit;

    public static FelveszDialog newInstance(FelveszListener listener) {
        FelveszDialog f = new FelveszDialog();
        f.layoutResId = R.layout.dialog_felvesz;
        f.listener = listener;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        azonEdit = (EditText) v.findViewById(R.id.teny_dialog_azon);
        toggleKeyboard();
        toggleKeyboard();

        Button ok = (Button) v.findViewById(R.id.dialog_felvesz_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok();
            }
        });

        Button cancel = (Button) v.findViewById(R.id.dialog_felvesz_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        return v;
    }

    public void cancel() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(azonEdit.getWindowToken(), 0);
        dismiss();
    }

    public void ok() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(azonEdit.getWindowToken(), 0);
        listener.onFelvesz(azonEdit.getText().toString());
        dismiss();
    }

    public void toggleKeyboard() {
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}

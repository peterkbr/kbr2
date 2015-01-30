package hu.flexisys.kbr.view.tenyeszet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by Peter on 2014.07.08..
 */
public class TorlesAlertDialog extends KbrDialog {

    private TorlesAlertListener listener;

    public static TorlesAlertDialog newInstance(TorlesAlertListener listener) {
        TorlesAlertDialog f = new TorlesAlertDialog();
        f.listener = listener;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_torles_alert;
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button ok = (Button) v.findViewById(R.id.dialog_torles_alert_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTorles();
            }
        });

        Button cancel = (Button) v.findViewById(R.id.dialog_torles_alert_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

}

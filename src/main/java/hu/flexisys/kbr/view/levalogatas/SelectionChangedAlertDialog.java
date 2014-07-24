package hu.flexisys.kbr.view.levalogatas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by Peter on 2014.07.08..
 */
public class SelectionChangedAlertDialog extends KbrDialog {

    private SelectionChangedAlertListener listener;

    public static SelectionChangedAlertDialog newInstance(SelectionChangedAlertListener listener) {
        SelectionChangedAlertDialog f = new SelectionChangedAlertDialog();
        f.layoutResId = R.layout.dialog_lev_selection_changed_alert;
        f.listener = listener;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button mentesEsKilepes = (Button) v.findViewById(R.id.lev_selection_changed_dialog_mentes_kilepes);
        mentesEsKilepes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMentesEsKilepes();
            }
        });

        Button kilepes = (Button) v.findViewById(R.id.lev_selection_changed_dialog_kilepes);
        kilepes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onKilepes();
            }
        });
        return v;
    }

}
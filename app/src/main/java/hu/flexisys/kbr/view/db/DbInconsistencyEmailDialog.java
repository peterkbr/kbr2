package hu.flexisys.kbr.view.db;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by peter on 06/09/14.
 */
public class DbInconsistencyEmailDialog extends KbrDialog {

    private DbInconsistenyEmailListener listener;

    public static KbrDialog newInstance(DbInconsistenyEmailListener listener) {
        DbInconsistencyEmailDialog f = new DbInconsistencyEmailDialog();
        f.listener = listener;
        f.setCancelable(false);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_db_inconsistency_email;
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button ok = (Button) v.findViewById(R.id.dialog_db_inc_email_button_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.handleDbIconsistency();
                dismiss();
            }
        });
        return v;
    }

    public interface DbInconsistenyEmailListener {
        public void handleDbIconsistency();
    }
}

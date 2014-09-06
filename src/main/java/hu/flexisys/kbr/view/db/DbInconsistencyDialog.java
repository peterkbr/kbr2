package hu.flexisys.kbr.view.db;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by peter on 06/09/14.
 */
public class DbInconsistencyDialog extends KbrDialog {

    private DbInconsistenyListener listener;
    private RadioButton innerRadioButton;
    private RadioButton sdcardRadioButton;
    private boolean inner;

    public static KbrDialog newInstance(DbInconsistenyListener listener) {
        DbInconsistencyDialog f = new DbInconsistencyDialog();
        f.layoutResId = R.layout.dialog_db_inconsistency;
        f.listener = listener;
        f.setCancelable(false);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        inner = true;
        innerRadioButton = (RadioButton) v.findViewById(R.id.dialog_db_inc_chb_inner);
        sdcardRadioButton = (RadioButton) v.findViewById(R.id.dialog_db_inc_chb_sdcard);
        update();

        innerRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inner = true;
                    update();
                }
            }
        });
        sdcardRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inner = false;
                    update();
                }
            }
        });

        Button ok = (Button) v.findViewById(R.id.dialog_db_inc_button_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.handleDbIconsistency(inner);
                dismiss();
            }
        });
        return v;
    }

    private void update() {
        innerRadioButton.setChecked(inner);
        sdcardRadioButton.setChecked(!inner);
    }

    public interface DbInconsistenyListener {
        public void handleDbIconsistency(boolean inner);
    }
}

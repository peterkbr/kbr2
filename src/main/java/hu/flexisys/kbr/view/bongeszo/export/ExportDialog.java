package hu.flexisys.kbr.view.bongeszo.export;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by peter on 25/08/14.
 */
public class ExportDialog extends KbrDialog {

    private ExportListener listener;

    public static KbrDialog newInstance(ExportListener listener) {
        ExportDialog dialog = new ExportDialog();
        dialog.layoutResId = R.layout.dialog_export;
        dialog.listener = listener;
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (layoutResId == 0) {
            layoutResId = R.layout.dialog_export;
        }
        final View v = super.onCreateView(inflater, container, savedInstanceState);

        Button ok = (Button) v.findViewById(R.id.dialog_export_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                CheckBox pdfCheckBox = (CheckBox) v.findViewById(R.id.dialog_export_checkBox_pdf);
                Boolean pdf = pdfCheckBox.isChecked();
                CheckBox csvCheckBox = (CheckBox) v.findViewById(R.id.dialog_export_checkBox_csv);
                Boolean csv = csvCheckBox.isChecked();
                if (!pdf && !csv) {
                    // TODO i18n
                    Toast.makeText(getActivity(), "Kérem válasszon formátumot!", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.onExport(pdf, csv);
            }
        });

        Button cancel = (Button) v.findViewById(R.id.dialog_export_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                dismiss();
            }
        });

        return v;
    }

    public interface ExportListener {
        public void onExport(boolean pdf, boolean csv);
    }
}

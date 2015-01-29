package hu.flexisys.kbr.view.levalogatas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by peter on 25/08/14.
 */
public class LevalogatasExportDialog extends KbrDialog {

    private ExportListener listener;

    public static KbrDialog newInstance(ExportListener listener) {
        LevalogatasExportDialog dialog = new LevalogatasExportDialog();
        dialog.layoutResId = R.layout.dialog_levalogatas_export;
        dialog.listener = listener;
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    Toast.makeText(getActivity(), "Kérem, válasszon formátumot!", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioGroup orderGroup = (RadioGroup) v.findViewById(R.id.order_radioGroup);
                int id = orderGroup.getCheckedRadioButtonId();
                RadioButton radio = (RadioButton) v.findViewById(id);
                String orderBy = null;
                if (radio != null) {
                    orderBy = radio.getText().toString();
                }
                listener.onExport(pdf, csv, orderBy);
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
        public void onExport(boolean pdf, boolean csv, String orderBy);
    }
}

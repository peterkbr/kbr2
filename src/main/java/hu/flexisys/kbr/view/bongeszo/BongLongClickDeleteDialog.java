package hu.flexisys.kbr.view.bongeszo;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.view.KbrDialog;


/**
 * Created by peter on 01/08/14.
 */
public class BongLongClickDeleteDialog extends KbrDialog {

    private BongLongClickDeleteDialogListener longClickDialogListener;
    private Egyed egyed;

    public static BongLongClickDeleteDialog newInstance(BongLongClickDeleteDialogListener longClickDialogListener, Egyed egyed) {
        BongLongClickDeleteDialog dialog = new BongLongClickDeleteDialog();
        dialog.layoutResId = R.layout.dialog_bong_long_click_delete;
        dialog.longClickDialogListener = longClickDialogListener;
        dialog.egyed = egyed;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        TextView enarTextView = (TextView) v.findViewById(R.id.bong_longClickDelete_title);
        String enar = String.valueOf(egyed.getAZONO());
        if (enar.length() == 10 && egyed.getORSKO().equals("HU")) {
            Spanned spanned = Html.fromHtml(
                    "Valóban törli az " + enar.substring(0, 5) + " <b><font color='red'>" + enar.substring(5, 9) + "</font></b> " + enar.substring(9) +
                            " ENAR számú egyed bírálatát?");
            enarTextView.setText(spanned);
        } else {
            enarTextView.setText("Valóban törli az " + enar + " ENAR számú egyed bírálatát?");
        }

        Button button = (Button) v.findViewById(R.id.bong_longClickDelete_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickDialogListener.onDelete();
            }
        });

        button = (Button) v.findViewById(R.id.bong_longClickDelete_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickDialogListener.onDismiss();
            }
        });
        return v;
    }

    public interface BongLongClickDeleteDialogListener {

        public void onDelete();

        public void onDismiss();
    }
}

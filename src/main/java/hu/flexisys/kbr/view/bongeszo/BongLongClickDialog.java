package hu.flexisys.kbr.view.bongeszo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;


/**
 * Created by peter on 01/08/14.
 */
public class BongLongClickDialog extends KbrDialog {

    private LongClickDialogListener longClickDialogListener;

    public static BongLongClickDialog newInstance(LongClickDialogListener longClickDialogListener) {
        BongLongClickDialog dialog = new BongLongClickDialog();
        dialog.layoutResId = R.layout.dialog_bong_long_click;
        dialog.longClickDialogListener = longClickDialogListener;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        Button button = (Button) v.findViewById(R.id.bong_longClick_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickDialogListener.onView();
            }
        });

        button = (Button) v.findViewById(R.id.bong_longClick_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickDialogListener.onDelete();
            }
        });

        button = (Button) v.findViewById(R.id.bong_longClick_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickDialogListener.onDismiss();
            }
        });
        return v;
    }

    public interface LongClickDialogListener {

        public void onView();

        public void onDelete();

        public void onDismiss();
    }
}

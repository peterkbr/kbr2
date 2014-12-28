package hu.flexisys.kbr.view.biralat.kereso;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by Peter on 2014.07.08..
 */
public class BirKerNotSelectedDialog extends KbrDialog {

    private BirKerNotselectedListener listener;
    private Egyed selectedEgyed;

    public static BirKerNotSelectedDialog newInstance(BirKerNotselectedListener listener, Egyed selectedEgyed) {
        BirKerNotSelectedDialog f = new BirKerNotSelectedDialog();
        f.layoutResId = R.layout.dialog_bir_ker_notselected;
        f.listener = listener;
        f.selectedEgyed = selectedEgyed;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Button ok = (Button) v.findViewById(R.id.bir_ker_dialog_notselected_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBiralSelected(selectedEgyed);
            }
        });

        Button cancel = (Button) v.findViewById(R.id.bir_ker_dialog_notselected_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancelSelection();
            }
        });
        return v;
    }

}

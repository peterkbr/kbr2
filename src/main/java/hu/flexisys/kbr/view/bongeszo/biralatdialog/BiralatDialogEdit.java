package hu.flexisys.kbr.view.bongeszo.biralatdialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.view.KbrDialog;
import hu.flexisys.kbr.view.biralat.biral.BiralFragment;

/**
 * Created by peter on 04/09/14.
 */
public class BiralatDialogEdit extends KbrDialog {

    private BiralFragment biralFragment;
    private BiralFragment.BiralFragmentContainer biralFragmentContainer;

    public static BiralatDialogEdit newInstance(BiralFragment.BiralFragmentContainer container) {
        BiralatDialogEdit f = new BiralatDialogEdit();
        f.layoutResId = R.layout.dialog_biralat_edit;
        f.biralFragmentContainer = container;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout fragContainer = (LinearLayout) v.findViewById(R.id.fragmentContainer);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);

        ll.setId(12345);

        biralFragment = BiralFragment.newInstance(biralFragmentContainer);
        getChildFragmentManager().beginTransaction().add(ll.getId(), biralFragment, "someTag1").commit();

        fragContainer.addView(ll);
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().finish();
    }

}

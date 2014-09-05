package hu.flexisys.kbr.view.levalogatas.biralatdialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;
import hu.flexisys.kbr.view.component.biralpanel.BiralPanel;
import hu.flexisys.kbr.view.component.biralpanel.BiralPanelElement;
import hu.flexisys.kbr.view.component.numpad.BiralatNumPadInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 04/09/14.
 */
public class BiralatDialogFragment extends Fragment {

    private Biralat biralat;
    private int num;
    private int sum;

    public static BiralatDialogFragment newInstance(Biralat biralat, int num, int sum) {
        BiralatDialogFragment f = new BiralatDialogFragment();
        f.biralat = biralat;
        f.num = num;
        f.sum = sum;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_biralat_dialog, container, false);
        TextView enarView = (TextView) view.findViewById(R.id.dialog_biralat_enar);
        enarView.setText(getString(R.string.lev_dialog_biralat_enar, biralat.getAZONO()));
        TextView counter = (TextView) view.findViewById(R.id.dialog_biralat_counter);
        counter.setText(getString(R.string.lev_dialog_biralat_counter, num, sum));
        TextView datumView = (TextView) view.findViewById(R.id.dialog_biralat_datum);
        datumView.setText(getString(R.string.lev_dialog_biralat_datum, DateUtil.formatDate(biralat.getBIRDA())));

        String tipus = ((KbrApplication) getActivity().getApplication()).getBiralatTipus();
        BiralatTipus biralatTipus = BiralatTipusUtil.getBiralatTipus(tipus);
        List<Integer> breakPoints = new ArrayList<Integer>();
        breakPoints.add(20);

        BiralPanel biralPanel = (BiralPanel) view.findViewById(R.id.dialog_biralat_panel);
        biralPanel.setUp(7);
        for (int i = 0; i < biralatTipus.szempontList.size(); i++) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(biralatTipus.szempontList.get(i));
            BiralPanelElement element = new BiralPanelElement(getActivity(), R.layout.component_dialog_biral_panel_element);
            TextView label = element.getLabel();
            String labelValue = szempont.rovidNev + ":";
            label.setText(labelValue);

            BiralatNumPadInput input = element.getInput();
            String value = biralat.getErtByKod(szempont.kod);
            input.setText(value);

            biralPanel.addElement(element);
            if (breakPoints.contains(i)) {
                biralPanel.addBreakPoint();
            }
        }

        // akako
        BiralPanelElement element = new BiralPanelElement(getActivity(), R.layout.component_dialog_biral_panel_element);
        TextView label = element.getLabel();
        label.setText("AK:");

        BiralatNumPadInput input = element.getInput();
        String akakoValue = String.valueOf(biralat.getAKAKO());
        if (akakoValue != null && !akakoValue.isEmpty() && !akakoValue.equals("0")) {
            input.setText(akakoValue);
        }

        biralPanel.addElement(element);
        return view;
    }
}
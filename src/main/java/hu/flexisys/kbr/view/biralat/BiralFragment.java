package hu.flexisys.kbr.view.biralat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.view.numpad.NumPad;
import hu.flexisys.kbr.view.numpad.NumPadInput;

import java.util.Date;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralFragment extends Fragment {

    private BiralatActivity activity;
    private NumPad numpad;
    private int[] inputIds = new int[]{R.id.bir_bir_input_1, R.id.bir_bir_input_2, R.id.bir_bir_input_3, R.id.bir_bir_input_4, R.id.bir_bir_input_5,
            R.id.bir_bir_input_6, R.id.bir_bir_input_7, R.id.bir_bir_input_8, R.id.bir_bir_input_9, R.id.bir_bir_input_10, R.id.bir_bir_input_11,
            R.id.bir_bir_input_12, R.id.bir_bir_input_13, R.id.bir_bir_input_14, R.id.bir_bir_input_15, R.id.bir_bir_input_16, R.id.bir_bir_input_17,
            R.id.bir_bir_input_18, R.id.bir_bir_input_19, R.id.bir_bir_input_20, R.id.bir_bir_input_21, R.id.bir_bir_input_22, R.id.bir_bir_input_23,
            R.id.bir_bir_input_24, R.id.bir_bir_input_25, R.id.bir_bir_input_26};
    private int currentInputId = -1;

    public static BiralFragment newInstance(BiralatActivity activity) {
        BiralFragment fragment = new BiralFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_biralat_biral, container, false);
        numpad = (NumPad) v.findViewById(R.id.bir_bir_numpad);
        currentInputId = inputIds[0];
        NumPadInput input = (NumPadInput) v.findViewById(currentInputId);
        input.select();
        numpad.setNumPadInput(input);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.onBiralFragmentResume();
    }

    // UPDATE THE VIEWS

    public void updateDetails(Egyed egyed) {
        View view = getView();
        TextView enarTextView = (TextView) view.findViewById(R.id.bir_bir_enar);
        String text = String.valueOf(egyed.getAZONO());
        if (text.length() == 10) {
            Spanned spanned = Html.fromHtml(text.substring(0, 5) + " <b>" + text.substring(5, 9) + "</b> " + text.substring(9));
            enarTextView.setText(spanned);
        } else {
            enarTextView.setText(text);
        }


        Boolean biralt = false;
        Date lastBiralatDate = null;
        for (Biralat biralat : egyed.getBiralatList()) {
            if (biralat.getFELTOLTETLEN()) {
                biralt = true;
            }
            if (biralat.getBIRDA() != null && (lastBiralatDate == null || lastBiralatDate.before(biralat.getBIRDA()))) {
                lastBiralatDate = biralat.getBIRDA();
            }
        }
        if (egyed.getUJ()) {
            enarTextView.setBackgroundColor(getResources().getColor(R.color.red));
        } else if (biralt) {
            enarTextView.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (egyed.getKIVALASZTOTT()) {
            enarTextView.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            enarTextView.setBackgroundColor(getResources().getColor(R.color.gray));
        }

        TextView lastbirTextView = (TextView) view.findViewById(R.id.bir_bir_lastbir);
        text = "Nincs bírálat";
        if (lastBiralatDate != null && lastBiralatDate.getTime() > 1) {
            text = DateUtil.formatDate(lastBiralatDate);
        }
        lastbirTextView.setText(text);

        TextView laktTextView = (TextView) view.findViewById(R.id.bir_bir_lakt);
        text = "Nem ellett";
        if (egyed.getELLDA() != null && egyed.getELLDA().getTime() > 1) {
            long diff = (new Date().getTime() - egyed.getELLDA().getTime()) / (1000 * 60 * 60 * 24);
            int diffInDays = Math.round(diff);
            text = String.valueOf(diffInDays);
        }
        laktTextView.setText(text);

        TextView esTextView = (TextView) view.findViewById(R.id.bir_bir_es);
        text = "-";
        if (egyed.getELLSO() != null) {
            text = String.valueOf(egyed.getELLSO());
        }
        esTextView.setText(text);
    }

    public void updateInputArea(Biralat biralat) {
    }

    public void selectInput(View view) {
        currentInputId = view.getId();
        for (int id : inputIds) {
            NumPadInput input = (NumPadInput) getView().findViewById(id);
            if (id == currentInputId) {
                input.select();
                numpad.setNumPadInput(input);
            } else {
                input.unSelect();
            }
        }
    }
}

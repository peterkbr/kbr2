package hu.flexisys.kbr.view.biralat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;
import hu.flexisys.kbr.view.component.numpad.BiralatNumPadInput;
import hu.flexisys.kbr.view.component.numpad.NumPad;
import hu.flexisys.kbr.view.component.numpad.NumPadInput;
import hu.flexisys.kbr.view.component.numpad.NumPadInputContainer;

import java.util.Date;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralFragment extends Fragment implements NumPadInputContainer {

    private static String TAG = "BiralFragment";
    private final int lastStep = 24;
    private BiralatActivity activity;
    private NumPad numpad;
    private int[] inputIds = new int[]{R.id.bir_bir_input_1, R.id.bir_bir_input_2, R.id.bir_bir_input_3, R.id.bir_bir_input_4, R.id.bir_bir_input_5,
            R.id.bir_bir_input_6, R.id.bir_bir_input_7, R.id.bir_bir_input_8, R.id.bir_bir_input_9, R.id.bir_bir_input_10, R.id.bir_bir_input_11,
            R.id.bir_bir_input_12, R.id.bir_bir_input_13, R.id.bir_bir_input_14, R.id.bir_bir_input_15, R.id.bir_bir_input_16, R.id.bir_bir_input_17,
            R.id.bir_bir_input_18, R.id.bir_bir_input_19, R.id.bir_bir_input_20, R.id.bir_bir_input_21, R.id.bir_bir_input_22, R.id.bir_bir_input_23,
            R.id.bir_bir_input_24, R.id.bir_bir_input_25, R.id.bir_bir_input_26};
    private int[] labelIds = new int[]{R.id.bir_bir_input_label_1, R.id.bir_bir_input_label_2, R.id.bir_bir_input_label_3, R.id.bir_bir_input_label_4, R.id.bir_bir_input_label_5,
            R.id.bir_bir_input_label_6, R.id.bir_bir_input_label_7, R.id.bir_bir_input_label_8, R.id.bir_bir_input_label_9, R.id.bir_bir_input_label_10, R.id.bir_bir_input_label_11,
            R.id.bir_bir_input_label_12, R.id.bir_bir_input_label_13, R.id.bir_bir_input_label_14, R.id.bir_bir_input_label_15, R.id.bir_bir_input_label_16, R.id.bir_bir_input_label_17,
            R.id.bir_bir_input_label_18, R.id.bir_bir_input_label_19, R.id.bir_bir_input_label_20, R.id.bir_bir_input_label_21, R.id.bir_bir_input_label_22, R.id.bir_bir_input_label_23,
            R.id.bir_bir_input_label_24, R.id.bir_bir_input_label_25, R.id.bir_bir_input_label_26};
    private int currentInputId = -1;
    private int currentInputStep = -1;
    private Boolean biralatStarted = false;

    public static BiralFragment newInstance(BiralatActivity activity) {
        BiralFragment fragment = new BiralFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_biralat_biral, container, false);
        numpad = (NumPad) v.findViewById(R.id.bir_bir_numpad);
        setupBiralatTipus(v);
        stepToNextInput(v);
        return v;
    }

    private void setupBiralatTipus(View view) {
        BiralatTipus biralatTipus = BiralatTipusUtil.getBiralatTipus("7");
        for (int i = 0; i < biralatTipus.szempontList.size(); i++) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(biralatTipus.szempontList.get(i));
            TextView label = (TextView) view.findViewById(labelIds[i]);
            label.setText(szempont.kod + " ");
            BiralatNumPadInput input = (BiralatNumPadInput) view.findViewById(inputIds[i]);
            input.setMaxLength(szempont.keszletEnd.length());
            input.setKeszletStart(szempont.keszletStart);
            input.setKeszletEnd(szempont.keszletEnd);
            input.setContainer(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.onBiralFragmentResume();
    }

    // UPDATE THE VIEWS

    public void updateView(Egyed selectedEgyedForBiral) {
        biralatStarted = false;
        updateDetails(selectedEgyedForBiral);
        updateGrid(null);
    }

    private void updateDetails(Egyed egyed) {
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

    private void updateGrid(Biralat biralat) {
        if (biralat == null) {
            clearGrid();
        }
    }

    private void clearGrid() {
        View view = getView();
        for (int id : inputIds) {
            BiralatNumPadInput input = (BiralatNumPadInput) view.findViewById(id);
            input.setText("");
        }
    }

    public void selectInput(View view, View inputView) {
        currentInputId = inputView.getId();
        for (int i = 0; i < inputIds.length; i++) {
            int id = inputIds[i];
            NumPadInput input = (NumPadInput) view.findViewById(id);
            if (id == currentInputId) {
                currentInputStep = i;
                input.select();
                numpad.setNumPadInput(input);
            } else {
                input.unSelect();
            }
        }
    }

    private void stepToNextInput(View view) {
        NumPadInput input = null;
        currentInputStep++;
        for (; currentInputStep <= lastStep; currentInputStep++) {
            currentInputId = inputIds[currentInputStep];
            input = (NumPadInput) view.findViewById(currentInputId);
            if (input.getText().toString().isEmpty()) {
                break;
            } else {
                input = null;
            }
        }
        if (input != null) {
            selectInput(view, input);
        }
    }

    @Override
    public void onMaxLengthReached() {
        stepToNextInput(getView());
    }

    @Override
    public void onInvalidInput() {
        Log.e(TAG, "Invalid input!");
    }

    @Override
    public void onInput() {
        biralatStarted = true;
    }

    // GETTERS, SETTERS

    public Boolean getBiralatStarted() {
        return biralatStarted;
    }

    public void setBiralatStarted(Boolean biralatStarted) {
        this.biralatStarted = biralatStarted;
    }
}

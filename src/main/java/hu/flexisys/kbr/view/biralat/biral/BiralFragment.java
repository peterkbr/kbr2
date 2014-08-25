package hu.flexisys.kbr.view.biralat.biral;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;
import hu.flexisys.kbr.view.biralat.BiralatActivity;
import hu.flexisys.kbr.view.component.numpad.BiralatNumPadInput;
import hu.flexisys.kbr.view.component.numpad.NumPad;
import hu.flexisys.kbr.view.component.numpad.NumPadInput;
import hu.flexisys.kbr.view.component.numpad.NumPadInputContainer;

import java.util.*;

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
    private int vpViewId = R.id.bir_bir_input_26;
    private int akakoViewId = R.id.bir_bir_input_27;
    private Map<Integer, BiralatSzempont> szempontMap;
    private int currentInputId;
    private int currentInputStep;
    private Boolean editing;
    private Boolean biralatStarted;
    private List<Integer> vpFormulaInputIdLis;
    private Map<Integer, String> vpFormulaWeightMap;

    public static BiralFragment newInstance(BiralatActivity activity) {
        BiralFragment fragment = new BiralFragment();
        fragment.activity = activity;
        fragment.editing = false;
        fragment.biralatStarted = false;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_biralat_biral, container, false);
        numpad = (NumPad) v.findViewById(R.id.bir_bir_numpad);
        setupBiralatTipus(v);
        return v;
    }

    private void setupBiralatTipus(View view) {
        String tipus = ((KbrApplication) activity.getApplication()).getBiralatTipus();
        BiralatTipus biralatTipus = BiralatTipusUtil.getBiralatTipus(tipus);
        szempontMap = new HashMap<Integer, BiralatSzempont>();

        vpFormulaInputIdLis = new ArrayList<Integer>();
        vpFormulaWeightMap = new HashMap<Integer, String>();

        List<String> vpFormulaSzempontList = new ArrayList<String>();
        Map<String, String> vpFormulaSzempontWeightMap = new HashMap<String, String>();
        for (String vpFormula : biralatTipus.vpFormulaList) {
            String[] values = vpFormula.split("/");
            vpFormulaSzempontList.add(values[0]);
            vpFormulaSzempontWeightMap.put(values[0], values[1]);
        }

        for (int i = 0; i < biralatTipus.szempontList.size(); i++) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(biralatTipus.szempontList.get(i));
            TextView label = (TextView) view.findViewById(labelIds[i]);
            label.setText(szempont.rovidNev + " ");
            BiralatNumPadInput input = (BiralatNumPadInput) view.findViewById(inputIds[i]);
            input.setMaxLength(szempont.keszletEnd.length());
            input.setKeszletStart(szempont.keszletStart);
            input.setKeszletEnd(szempont.keszletEnd);
            input.setContainer(this);

            szempontMap.put(inputIds[i], szempont);

            if (vpFormulaSzempontList.contains(szempont.kod)) {
                vpFormulaInputIdLis.add(inputIds[i]);
                vpFormulaWeightMap.put(inputIds[i], vpFormulaSzempontWeightMap.get(szempont.kod));
            }
        }

        // akako
        final BiralatNumPadInput input = (BiralatNumPadInput) view.findViewById(akakoViewId);
        input.setMaxLength(1);
        input.setKeszletStart("1");
        input.setKeszletEnd("5");
        input.setContainer(new NumPadInputContainer() {
            @Override
            public void onMaxLengthReached() {
                activity.onAkako(input.getText().toString());
            }

            @Override
            public void onInvalidInput() {
                BiralFragment.this.onInvalidInput();
            }

            @Override
            public void onInput() {
                biralatStarted = true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.onBiralFragmentResume();
    }

    public void clearCurrentBiralat() {
        biralatStarted = false;
        editing = true;
        clearGrid();
    }

    public void updateCurrentBiralatWithAkako(String text) {
        if (text == null || text.isEmpty()) {
            editing = true;
        } else {
            if (text.equals("3")) {
                editing = true;
            } else {
                editing = false;
                clearGrid();
            }
            BiralatNumPadInput akakoInput = (BiralatNumPadInput) getView().findViewById(akakoViewId);
            akakoInput.setText(text);
            akakoInput.unSelect();
        }
    }

    private Integer calcVp() {
        View view = getView();
        Integer valueSum = 0;
        Integer weightSum = 0;
        for (Integer id : vpFormulaInputIdLis) {
            TextView textView = (TextView) view.findViewById(id);
            String valueString = textView.getText().toString();
            Integer value = Integer.valueOf(valueString);
            Integer weight = Integer.valueOf(vpFormulaWeightMap.get(id));
            valueSum += (value * weight);
            weightSum += weight;
        }
        Double vpDoubleValue = Double.valueOf(valueSum) / Double.valueOf(weightSum);
        int vp = (int) Math.round(vpDoubleValue);
        return vp;
    }

    public Map<String, String> getKodErtMap() {
        Map<String, String> map = new HashMap<String, String>();

        View view = getView();
        for (Integer viewId : szempontMap.keySet()) {
            String kod = szempontMap.get(viewId).kod;
            String ert = ((TextView) view.findViewById(viewId)).getText().toString();
            if (ert == null || ert.isEmpty()) {
                return null;
            }
            map.put(kod, ert);
        }
        return map;
    }

    public String getAkako() {
        return ((TextView) getView().findViewById(akakoViewId)).getText().toString();
    }

    // UPDATE THE VIEWS
    public void updateFragmentWithEgyed(Egyed selectedEgyedForBiral) {
        updateFragmentWithEgyed(selectedEgyedForBiral, true);
    }

    public void updateFragmentWithEgyed(Egyed selectedEgyedForBiral, Boolean editable) {
        clearDetails();
        clearGrid();
        if (selectedEgyedForBiral == null) {
            editing = false;
            biralatStarted = false;
        } else {
            editing = true;
            updateDetails(selectedEgyedForBiral);
            Biralat lastBiralat = null;
            for (Biralat biralat : selectedEgyedForBiral.getBiralatList()) {
                if (biralat.getFELTOLTETLEN() && !biralat.getEXPORTALT()) {
                    lastBiralat = biralat;
                }
            }
            updateGrid(lastBiralat);

            if (lastBiralat != null) {
                String akakoValue = String.valueOf(lastBiralat.getAKAKO());
                if (akakoValue != null && !akakoValue.isEmpty() && !akakoValue.equals("0")) {
                    if (!akakoValue.equals("3")) {
                        editing = false;
                        clearGrid();
                    }
                    updateAkakoView(akakoValue);
                }
            }
            if (editing && editable) {
                currentInputId = -1;
                currentInputStep = -1;
                stepToNextInput(getView());
            } else {
                editing = false;
                numpad.setNumPadInput(null);
            }
        }
    }

    private void updateDetails(Egyed egyed) {
        View view = getView();
        TextView enarTextView = (TextView) view.findViewById(R.id.bir_bir_enar);
        View detailsView = view.findViewById(R.id.bir_bir_details_layout);

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

        if (egyed.getUJ()) {
            detailsView.setVisibility(View.INVISIBLE);
        } else {
            detailsView.setVisibility(View.VISIBLE);
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
    }

    private void clearDetails() {
        View view = getView();
        TextView enarTextView = (TextView) view.findViewById(R.id.bir_bir_enar);
        enarTextView.setText("");
        enarTextView.setBackgroundColor(getResources().getColor(R.color.green));
        View detailsView = view.findViewById(R.id.bir_bir_details_layout);
        detailsView.setVisibility(View.INVISIBLE);
    }

    private void updateGrid(Biralat biralat) {
        clearGrid();
        if (biralat != null) {
            View view = getView();
            for (int i = 0; i < inputIds.length; i++) {
                int id = inputIds[i];
                BiralatSzempont szempont = szempontMap.get(id);
                BiralatNumPadInput input = (BiralatNumPadInput) view.findViewById(id);
                String value = biralat.getErtByKod(szempont.kod);
                input.setText(value);
                input.unSelect();
            }
        }
    }

    public void clearGrid() {
        View view = getView();
        for (int id : inputIds) {
            BiralatNumPadInput input = (BiralatNumPadInput) view.findViewById(id);
            input.setText("");
            input.unSelect();
        }
        BiralatNumPadInput akakoInput = (BiralatNumPadInput) view.findViewById(akakoViewId);
        akakoInput.setText("");
        akakoInput.unSelect();
    }

    private void updateAkakoView(String akako) {
        BiralatNumPadInput akakoInput = (BiralatNumPadInput) getView().findViewById(akakoViewId);
        akakoInput.setText(akako);
        akakoInput.unSelect();
    }

    public void clearAkakoView() {
        updateAkakoView("");
    }

    public void selectInput(View view, View inputView) {
        NumPadInput input = null;
        if (!editing) {
            input = (NumPadInput) view.findViewById(akakoViewId);
            String akakoText = input.getText().toString();
            if (akakoText != null && !akakoText.isEmpty() && inputView.getId() == akakoViewId) {
                input.select();
                numpad.setNumPadInput(input);
            } else {
                input.unSelect();
            }
            return;
        }
        currentInputId = inputView.getId();
        for (int i = 0; i < inputIds.length; i++) {
            int id = inputIds[i];
            input = (NumPadInput) view.findViewById(id);
            if (id == currentInputId) {
                currentInputStep = i;
                input.select();
                numpad.setNumPadInput(input);
            } else {
                input.unSelect();
            }
        }

        input = (NumPadInput) view.findViewById(akakoViewId);
        if (currentInputId == akakoViewId) {
            input.select();
            numpad.setNumPadInput(input);
        } else {
            input.unSelect();
        }
    }

    private void stepToNextInput(View view) {
        if (!editing) {
            return;
        }
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
        if (currentInputStep > lastStep) {
            Integer vp = calcVp();
            updateVpView(String.valueOf(vp));
        }
        if (input != null) {
            selectInput(view, input);
        }
    }

    private void updateVpView(String vp) {
        TextView vpView = (TextView) getView().findViewById(vpViewId);
        vpView.setText(vp);
    }

    @Override
    public void onMaxLengthReached() {
        stepToNextInput(getView());
    }

    @Override
    public void onInvalidInput() {
        activity.beep();
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

    public void setBiralatSaved() {
        biralatStarted = false;
    }

}

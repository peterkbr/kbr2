package hu.flexisys.kbr.view.biralat.biral;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.PropertiesUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;
import hu.flexisys.kbr.view.component.biralpanel.BiralPanel;
import hu.flexisys.kbr.view.component.biralpanel.BiralPanelElement;
import hu.flexisys.kbr.view.component.numpad.BiralatNumPadInput;
import hu.flexisys.kbr.view.component.numpad.NumPad;
import hu.flexisys.kbr.view.component.numpad.NumPadInputContainer;

import java.io.IOException;
import java.util.*;

/**
 * Created by Peter on 2014.07.11..
 */
public class BiralFragment extends Fragment implements NumPadInputContainer {

    private static String TAG = "KBR2_BiralFragment";
    private BiralFragmentContainer container;
    private NumPad numpad;
    private BiralPanel biralPanel;

    private Map<String, BiralatNumPadInput> szempontKodInputMap;
    private List<BiralatNumPadInput> biralatNumPadInputs;
    private BiralatNumPadInput vpBiralatNumPadInput;
    private BiralatNumPadInput akakoBiralatNumPadInput;
    private List<String> vpFormulaSzempontKodList;
    private Map<String, String> vpFormulaWeightMap;
    private Boolean editing;
    private Boolean biralatStarted;

    public static BiralFragment newInstance(BiralFragmentContainer container) {
        BiralFragment fragment = new BiralFragment();
        fragment.container = container;
        fragment.editing = false;
        fragment.biralatStarted = false;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_biralat_biral, container, false);
        numpad = (NumPad) v.findViewById(R.id.bir_bir_numpad);
        biralPanel = (BiralPanel) v.findViewById(R.id.bir_bir_panel);
        setupBiralatTipus();
        return v;
    }

    private void setupBiralatTipus() {
        int currentId = 123;
        biralatNumPadInputs = new ArrayList<BiralatNumPadInput>();
        Properties layoutProperties = null;
        try {
            layoutProperties = PropertiesUtil.loadProperties(getActivity(), R.raw.biralat_tipus_layout);
        } catch (IOException e) {
            Log.e(TAG, "Error while loading colors", e);
        }

        String tipus = ((KbrApplication) getActivity().getApplication()).getBiralatTipus();
        BiralatTipus biralatTipus = BiralatTipusUtil.getBiralatTipus(tipus);
        szempontKodInputMap = new HashMap<String, BiralatNumPadInput>();

        vpFormulaSzempontKodList = new ArrayList<String>();
        vpFormulaWeightMap = new HashMap<String, String>();
        for (String vpFormula : biralatTipus.vpFormulaList) {
            String[] values = vpFormula.split("/");
            vpFormulaSzempontKodList.add(values[0]);
            vpFormulaWeightMap.put(values[0], values[1]);
        }

        int maxHeight = 7;
        List<Integer> breakPoints = new ArrayList<Integer>();
        breakPoints.add(20);
        biralPanel.setUp(maxHeight);

        for (int i = 0; i < biralatTipus.szempontList.size(); i++) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(biralatTipus.szempontList.get(i));

            BiralPanelElement element = new BiralPanelElement(getActivity());
            BiralatNumPadInput input = element.getInput();
            LinearLayout layout = element.getLayout();
            TextView label = element.getLabel();

            String labelValue = szempont.rovidNev;
            String colorCode = null;
            if (layoutProperties != null) {
                colorCode = layoutProperties.getProperty(biralatTipus.id + "_" + szempont.kod);
            }
            if (colorCode != null) {
                layout.setBackgroundColor(Color.parseColor(colorCode));
            }
            label.setText(labelValue);
            input.setMaxLength(szempont.keszletEnd.length());
            input.setKeszletStart(szempont.keszletStart);
            input.setKeszletEnd(szempont.keszletEnd);
            input.setContainer(this);
            input.setId(currentId++);
            input.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BiralFragment.this.selectInput(v);
                }
            });
            biralatNumPadInputs.add(input);

            szempontKodInputMap.put(szempont.kod, input);

            biralPanel.addElement(element);
            if (breakPoints.contains(i)) {
                biralPanel.addBreakPoint();
            }
        }
        vpBiralatNumPadInput = biralatNumPadInputs.get(biralatNumPadInputs.size() - 1);

        // akako
        BiralPanelElement element = new BiralPanelElement(getActivity());
        akakoBiralatNumPadInput = element.getInput();
        LinearLayout layout = element.getLayout();
        TextView label = element.getLabel();

        layout.setBackgroundColor(getResources().getColor(R.color.red));
        label.setText("AK ");
        akakoBiralatNumPadInput.setMaxLength(1);
        akakoBiralatNumPadInput.setKeszletStart("1");
        akakoBiralatNumPadInput.setKeszletEnd("5");
        akakoBiralatNumPadInput.setContainer(new NumPadInputContainer() {
            @Override
            public void onMaxLengthReached() {
                container.onAkako(akakoBiralatNumPadInput.getText().toString());
            }

//            @Override
//            public void onInvalidInput() {
//                BiralFragment.this.onInvalidInput();
//            }
//
//            @Override
//            public void onValidInput() {
//                BiralFragment.this.onValidInput();
//            }

            @Override
            public void onInput() {
                biralatStarted = true;
            }
        });
        akakoBiralatNumPadInput.setId(currentId);
        akakoBiralatNumPadInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BiralFragment.this.selectInput(v);
            }
        });
        biralPanel.addElement(element);
    }

    @Override
    public void onResume() {
        super.onResume();
        container.onBiralFragmentResume(this);
    }

    public void clearCurrentBiralat() {
        biralatStarted = false;
        editing = true;
        clearGrid();
    }

    public void updateCurrentBiralatWithAkako(String text) {
        if (text == null || text.isEmpty()) {
            editing = true;
            clearAkakoView();
            stepToNextInput();
        } else {
            if (text.equals("3")) {
                editing = true;
            } else {
                editing = false;
                clearGrid();
            }
            akakoBiralatNumPadInput.setText(text);
            akakoBiralatNumPadInput.unSelect();
        }
    }

    private Integer calcVp() {
        Integer valueSum = 0;
        Integer weightSum = 0;
        for (String kod : vpFormulaSzempontKodList) {
            BiralatNumPadInput input = szempontKodInputMap.get(kod);
            String valueString = input.getText().toString();
            Integer value = Integer.valueOf(valueString);
            Integer weight = Integer.valueOf(vpFormulaWeightMap.get(kod));
            valueSum += (value * weight);
            weightSum += weight;
        }
        Double vpDoubleValue = Double.valueOf(valueSum) / Double.valueOf(weightSum);
        int vp = (int) Math.round(vpDoubleValue);
        return vp;
    }

    public Map<String, String> getKodErtMap() {
        Map<String, String> map = new HashMap<String, String>();

        for (String kod : szempontKodInputMap.keySet()) {
            BiralatNumPadInput input = szempontKodInputMap.get(kod);
            String ert = input.getText().toString();
            if (ert == null || ert.isEmpty()) {
                return null;
            }
            map.put(kod, ert);
        }
        return map;
    }

    public String getAkako() {
        return akakoBiralatNumPadInput.getText().toString();
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
                if (editable && biralat.getLETOLTOTT()) {
                    continue;
                }
                if (lastBiralat == null || lastBiralat.getBIRDA().getTime() < biralat.getBIRDA().getTime()) {
                    lastBiralat = biralat;
                }
            }
            updateGrid(lastBiralat);

            if (lastBiralat != null) {
                if (lastBiralat.getFELTOLTETLEN() && !lastBiralat.getEXPORTALT()) {
                    editing = true;
                } else {
                    editing = false;
                }

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
                stepToNextInput();
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
            enarTextView.setBackgroundColor(getResources().getColor(R.color.light_blue));
        } else if (egyed.getKIVALASZTOTT()) {
            enarTextView.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            enarTextView.setBackgroundColor(getResources().getColor(R.color.transparent));
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
            for (String kod : szempontKodInputMap.keySet()) {
                BiralatNumPadInput input = szempontKodInputMap.get(kod);
                String value = biralat.getErtByKod(kod);
                input.setText(value);
                input.unSelect();
            }
        }
    }

    public void clearGrid() {
        for (BiralatNumPadInput input : biralatNumPadInputs) {
            input.setText("");
            input.unSelect();
        }
        akakoBiralatNumPadInput.setText("");
        akakoBiralatNumPadInput.unSelect();
    }

    private void updateAkakoView(String akako) {
        akakoBiralatNumPadInput.setText(akako);
        akakoBiralatNumPadInput.unSelect();
    }

    public void clearAkakoView() {
        updateAkakoView("");
    }

    public void selectInput(View inputView) {
        if (!editing) {
            String akakoText = akakoBiralatNumPadInput.getText().toString();
            if (akakoText != null && !akakoText.isEmpty() && inputView.getId() == akakoBiralatNumPadInput.getId()) {
                akakoBiralatNumPadInput.select();
                numpad.setNumPadInput(akakoBiralatNumPadInput);
            } else {
                akakoBiralatNumPadInput.unSelect();
            }
            return;
        }

        for (BiralatNumPadInput input : biralatNumPadInputs) {
            if (input.getId() == inputView.getId()) {
                input.select();
                numpad.setNumPadInput(input);
            } else {
                input.unSelect();
            }
        }
        if (inputView.getId() == akakoBiralatNumPadInput.getId()) {
            akakoBiralatNumPadInput.select();
            numpad.setNumPadInput(akakoBiralatNumPadInput);
        } else {
            akakoBiralatNumPadInput.unSelect();
        }
    }

    public void selectInputByKod(String kod) {
        BiralatNumPadInput input = this.szempontKodInputMap.get(kod);
        selectInput(input);
    }

    private void stepToNextInput() {
        if (!editing) {
            return;
        }
        for (BiralatNumPadInput input : biralatNumPadInputs) {
            if (input.getId() == vpBiralatNumPadInput.getId()) {
                Integer vp = calcVp();
                vpBiralatNumPadInput.setText(String.valueOf(vp));
                break;
            }
            if (input.getText().toString().isEmpty()) {
                selectInput(input);
                break;
            }
        }
    }

    @Override
    public void onMaxLengthReached() {
        stepToNextInput();
    }

//    @Override
//    public void onInvalidInput() {
////        container.beep();
//        SoundUtil.errorBeep();
//    }
//
//    @Override
//    public void onValidInput() {
////        container.beep();
//        SoundUtil.buttonBeep();
//    }

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

    public void forceEditing() {
        editing = true;
    }

    public String invalidErtAtKod() {
        String tipus = ((KbrApplication) getActivity().getApplication()).getBiralatTipus();
        BiralatTipus biralatTipus = BiralatTipusUtil.getBiralatTipus(tipus);
        for (int i = 0; i < biralatTipus.szempontList.size(); i++) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(biralatTipus.szempontList.get(i));
            BiralatNumPadInput input = szempontKodInputMap.get(szempont.kod);
            String stringValue = input.getText().toString();
            if (stringValue == null && stringValue.isEmpty()) {
                continue;
            } else {
                Integer value = Integer.valueOf(stringValue);
                if (value < Integer.valueOf(szempont.keszletStart) || value > Integer.valueOf(szempont.keszletEnd)) {
                    return szempont.kod;
                }
            }
        }
        return null;
    }

    public interface BiralFragmentContainer {
        public void onAkako(String akako);

        public void onBiralFragmentResume(BiralFragment biralFragment);
    }
}

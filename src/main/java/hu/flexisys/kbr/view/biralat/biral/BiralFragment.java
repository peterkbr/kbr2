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
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.LogUtil;
import hu.flexisys.kbr.util.PropertiesUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;
import hu.flexisys.kbr.view.biralat.biral.vp.VPType;
import hu.flexisys.kbr.view.biralat.biral.vp.VPTypeUtil;
import hu.flexisys.kbr.view.component.biralpanel.BiralPanel;
import hu.flexisys.kbr.view.component.biralpanel.BiralPanelElement;
import hu.flexisys.kbr.view.component.numpad.BiralatNumPadInput;
import hu.flexisys.kbr.view.component.numpad.NumPad;
import hu.flexisys.kbr.view.component.numpad.NumPadInputContainer;

import java.io.IOException;
import java.util.*;

public class BiralFragment extends Fragment implements NumPadInputContainer {

    private BiralFragmentContainer container;
    private NumPad numpad;
    private BiralPanel biralPanel;

    private Map<String, BiralatNumPadInput> szempontKodInputMap;
    private Map<Integer, String> inputIdSzempontKodMap;
    private List<String> szempontKodList;
    private String currentSzempontKod;
    private List<BiralatNumPadInput> biralatNumPadInputs;
    private BiralatNumPadInput akakoBiralatNumPadInput;
    private Boolean editing;
    private Boolean biralatStarted;

    private String megjegyzes;

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
        return v;
    }

    private List<String> vpKodList;

    public void setupBiralatTipus() {
        int currentId = 123;
        biralatNumPadInputs = new ArrayList<BiralatNumPadInput>();
        Properties layoutProperties = null;
        try {
            layoutProperties = PropertiesUtil.loadProperties(getActivity(), R.raw.biralat_tipus_layout);
        } catch (IOException e) {
            Log.e(LogUtil.TAG, "Error while loading colors", e);
        }
        String tipus = BiralatTipusUtil.currentBiralatTipus;
        BiralatTipus biralatTipus = BiralatTipusUtil.getBiralatTipus(tipus);
        szempontKodInputMap = new HashMap<String, BiralatNumPadInput>();
        inputIdSzempontKodMap = new HashMap<Integer, String>();

        vpKodList = biralatTipus.vpList;

        int maxHeight = 7;
        List<Integer> breakPoints = new ArrayList<Integer>();
        breakPoints.add(20);
        biralPanel.setUp(maxHeight);

        szempontKodList = new ArrayList<String>();
        currentSzempontKod = null;
        for (int i = 0; i < biralatTipus.szempontList.size(); i++) {
            final BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(biralatTipus.szempontList.get(i));

            BiralPanelElement element;
            if (szempont.keszletEnd.length() > 1) {
                element = new BiralPanelElement(getActivity(), R.layout.component_biral_panel_element_double);
            } else {
                element = new BiralPanelElement(getActivity());
            }
            BiralatNumPadInput input = element.getInput();
            LinearLayout layout = element.getLayout();
            TextView label = element.getLabel();

            String labelValue = szempont.rovidNev;
            String colorCode = null;
            if (layoutProperties != null) {
                colorCode = layoutProperties.getProperty(szempont.kod);
            }
            if (colorCode != null) {
                layout.setBackgroundColor(Color.parseColor(colorCode));
            }
            label.setText(labelValue);
            input.setKeszletStart(szempont.keszletStart);
            input.setKeszletEnd(szempont.keszletEnd);
            input.setKeszletExtensions(szempont.keszletExtensions);
            input.setContainer(this);
            input.setId(currentId++);
            inputIdSzempontKodMap.put(input.getId(), szempont.kod);
            input.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String kod = inputIdSzempontKodMap.get(v.getId());
                    BiralFragment.this.selectInputByKod(kod);
                }
            });
            if (biralatTipus.vpList.contains(szempont.kod)) {
                input.setClickable(false);
            }

            biralatNumPadInputs.add(input);

            szempontKodList.add(szempont.kod);
            szempontKodInputMap.put(szempont.kod, input);

            biralPanel.addElement(element);
            if (breakPoints.contains(i)) {
                biralPanel.addBreakPoint();
            }
        }

        // akako
        BiralPanelElement element = new BiralPanelElement(getActivity(), R.layout.component_biral_panel_element_double);
        akakoBiralatNumPadInput = element.getInput();
        LinearLayout layout = element.getLayout();
        TextView label = element.getLabel();

        layout.setBackgroundColor(getResources().getColor(R.color.orange_red));
        label.setText("AK ");
        akakoBiralatNumPadInput.setMaxLength(1);
        akakoBiralatNumPadInput.setKeszletStart("1");
        akakoBiralatNumPadInput.setKeszletEnd("5");
        akakoBiralatNumPadInput.setContainer(new NumPadInputContainer() {
            @Override
            public void onMaxLengthReached() {
                container.onAkako(akakoBiralatNumPadInput.getText().toString());
            }

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
                currentSzempontKod = null;
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
        container.selectBiralat(null);
        megjegyzes = null;
        if (selectedEgyedForBiral == null) {
            editing = false;
            biralatStarted = false;
        } else {
            editing = true;
            updateDetails(selectedEgyedForBiral);
            Biralat lastBiralat = null;
            Biralat oldBiralat = null;
            for (Biralat biralat : selectedEgyedForBiral.getBiralatList()) {
                if (editable && biralat.getLETOLTOTT()) {
                    if (oldBiralat == null || oldBiralat.getBIRDA().getTime() < biralat.getBIRDA().getTime()) {
                        oldBiralat = biralat;
                    }
                    continue;
                }
                if (lastBiralat == null || lastBiralat.getBIRDA().getTime() < biralat.getBIRDA().getTime()) {
                    lastBiralat = biralat;
                }
            }
            if (lastBiralat == null && oldBiralat == null) {
                updateGridForNew();
            } else if (lastBiralat == null) {
                updateGrid(oldBiralat, true);
            } else {
                container.selectBiralat(lastBiralat);
                updateGrid(lastBiralat);
                if (lastBiralat != null) {
                    megjegyzes = lastBiralat.getMEGJEGYZES();

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
        enarTextView.setClickable(true);
        enarTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editing) {
                    container.openMegjegyzesDialog(megjegyzes);
                }
            }
        });

        View detailsView = view.findViewById(R.id.bir_bir_details_layout);

        String text = String.valueOf(egyed.getAZONO());
        if ("HU".equals(egyed.getORSKO()) && text.length() == 10) {
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

    private void updateGridForNew() {
        for (String kod : szempontKodInputMap.keySet()) {
            BiralatNumPadInput input = szempontKodInputMap.get(kod);
            input.newInput();
            input.unSelect();
        }
    }

    private void updateGrid(Biralat biralat) {
        clearGrid();
        updateGrid(biralat, false);
    }

    private void updateGrid(Biralat biralat, boolean old) {
        clearGrid();
        if (biralat != null) {
            for (String kod : szempontKodInputMap.keySet()) {
                BiralatNumPadInput input = szempontKodInputMap.get(kod);
                String value = biralat.getErtByKod(kod);
                if (old) {
                    input.addOldContent(value);
                } else {
                    input.setText(value);
                }
                input.unSelect();
            }
        }
    }

    public void clearGrid() {
        for (BiralatNumPadInput input : biralatNumPadInputs) {
            input.clear();
        }
        akakoBiralatNumPadInput.setText("");
        akakoBiralatNumPadInput.unSelect();
        currentSzempontKod = null;
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

        if (inputView == null) {
            for (BiralatNumPadInput input : biralatNumPadInputs) {
                input.unSelect();
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
        currentSzempontKod = kod;
        BiralatNumPadInput input = null;
        if (kod != null) {
            input = this.szempontKodInputMap.get(kod);
        }
        selectInput(input);
    }

    private void stepToNextInput() {
        if (!editing) {
            return;
        }

        if (currentSzempontKod == null) {
            currentSzempontKod = szempontKodList.get(0);
        } else {
            for (int i = 0; i < szempontKodList.size(); i++) {
                String szempontKod = szempontKodList.get(i);
                if (szempontKod.equals(currentSzempontKod)) {
                    currentSzempontKod = null;
                    for (int j = ++i; j < szempontKodList.size(); j++) {
                        szempontKod = szempontKodList.get(j);
                        if (szempontKodInputMap.get(szempontKod).isClickable()) {
                            currentSzempontKod = szempontKod;
                            break;
                        }
                    }
                    break;
                }
            }
        }

        selectInputByKod(currentSzempontKod);
    }

    @Override
    public void onMaxLengthReached() {
        String akakoString = getAkako();
        if (getBiralatStarted() && (akakoString == null || akakoString.isEmpty() || akakoString.equals("3"))) {
            calcVp();
        }
        stepToNextInput();
    }

    private void calcVp() {
        for (String vpKod : vpKodList) {
            VPType vp = VPType.parseVPType(Integer.valueOf(vpKod));
            if (vp == null) {
                continue;
            }
            List<String> list = VPTypeUtil.getSzempontSzarmaztatasList(vpKod);
            List<Integer> szempontEretekList = new ArrayList<Integer>();

            for (String kod : list) {
                BiralatNumPadInput input = szempontKodInputMap.get(kod);
                String valueString = input.getText().toString();
                if (valueString.length() > 0) {
                    Integer value = Integer.valueOf(valueString);
                    szempontEretekList.add(value);
                }
            }

            Integer vpErtek = vp.evaluateParams(szempontEretekList);
            if (vpErtek != null) {
                BiralatNumPadInput vpInput = szempontKodInputMap.get(vpKod);
                vpInput.removeSpecialContent();
                vpInput.setText(String.valueOf(vpErtek));
            }
        }
    }

    @Override
    public void onInput() {
        biralatStarted = true;
    }

    // GETTERS, SETTERS

    public Boolean getBiralatStarted() {
        return biralatStarted;
    }

    public void setBiralatSaved() {
        biralatStarted = false;
    }

    public void forceEditing() {
        editing = true;
    }

    public String invalidErtAtKod() {
        String tipus = BiralatTipusUtil.currentBiralatTipus;
        BiralatTipus biralatTipus = BiralatTipusUtil.getBiralatTipus(tipus);
        for (int i = 0; i < biralatTipus.szempontList.size(); i++) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(biralatTipus.szempontList.get(i));
            BiralatNumPadInput input = szempontKodInputMap.get(szempont.kod);
            String stringValue = input.getText().toString();
            if (!stringValue.isEmpty()) {
                Integer value = Integer.valueOf(stringValue);
                boolean validForStart = true;
                if (value < Integer.valueOf(szempont.keszletStart)) {
                    validForStart = false;
                }
                boolean validForEnd = true;
                if (value > Integer.valueOf(szempont.keszletEnd)) {
                    validForEnd = false;
                }
                if (!validForStart || !validForEnd) {
                    boolean valid = false;
                    for (String ke : szempont.keszletExtensions) {
                        if (ke.equals(stringValue)) {
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        return szempont.kod;
                    }
                }
            }
        }
        return null;
    }

    public String getMegjegyzes() {
        return megjegyzes;
    }

    public void setMegjegyzes(String megjegyzes) {
        this.megjegyzes = megjegyzes;
        biralatStarted = true;
    }

    public interface BiralFragmentContainer {

        void onAkako(String akako);

        void onBiralFragmentResume(BiralFragment biralFragment);

        void selectBiralat(Biralat lastBiralat);

        void openMegjegyzesDialog(String megjegyzes);

    }
}

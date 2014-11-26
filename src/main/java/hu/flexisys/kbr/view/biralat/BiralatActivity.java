package hu.flexisys.kbr.view.biralat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.LogUtil;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.NotificationDialog;
import hu.flexisys.kbr.view.biralat.biral.*;
import hu.flexisys.kbr.view.biralat.kereso.*;
import hu.flexisys.kbr.view.component.numpad.NumPad;
import hu.flexisys.kbr.view.component.numpad.NumPadInput;

import java.util.*;

/**
 * Created by Peter on 2014.07.04..
 */
public class BiralatActivity extends KbrActivity implements BirKerNotfoundListener, BirKerEgyedListDialog.EgyedListDialogContainer {

    private String[] selectedTenazArray;
    private BiralatPagerAdapter adapter;
    private NumPadInput hasznalatiInput;
    private List<Egyed> egyedList;
    private List<Egyed> biraltEgyedList;
    private List<Egyed> biralandoEgyedList;
    private Egyed selectedEgyed;
    private Biralat selectedBiralat;
    private Boolean hu;
    private KeresoFragment keresoFragment;
    private BiralFragment biralFragment;
    private Boolean showingEgyedListDialog = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.activity_biralat);

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        ViewPager pager = (ViewPager) findViewById(R.id.biralat_pager);
        adapter = new BiralatPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(adapter);

        BiralatPageChangeListener biralatPageChangeListener = new BiralatPageChangeListener(actionBar);
        pager.setOnPageChangeListener(biralatPageChangeListener);

        BiralatTabListener biralatTabListener = new BiralatTabListener(this, pager);

        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.back).setTabListener(biralatTabListener));
        actionBar.addTab(actionBar.newTab().setText("Kereső").setTabListener(biralatTabListener));
        actionBar.addTab(actionBar.newTab().setText("Bírál").setTabListener(biralatTabListener));
        actionBar.selectTab(actionBar.getTabAt(1));

        selectedTenazArray = getIntent().getExtras().getStringArray(BiralatTenyeszetActivity.EXTRAKEY_SELECTEDTENAZLIST);
        reloadData();

        hu = true;
    }

    @Override
    public void onBackPressed() {
        if (actionBar.getSelectedTab().getPosition() == 2) {
            actionBar.selectTab(actionBar.getTabAt(1));
        } else {
            onExit();
        }
    }

    public void onExit() {
        String akakoString = biralFragment.getAkako();
        Map<String, String> map = biralFragment.getKodErtMap();
        if ((biralFragment.getBiralatStarted() && (akakoString == null || akakoString.isEmpty() || akakoString.equals("3")) && map == null) ||
                biralFragment.getBiralatStarted()) {
            FragmentTransaction ft = getFragmentTransactionWithTag("exit");
            dialog = BirBirExitBiralatDialog.newInstance(new BirBirExitBiralatDialog.BirBirExitBiralatDialogListener() {
                @Override
                public void onBirBirExitBiralatOk() {
                    dismissDialog();
                    finish();
                }

                @Override
                public void onBirBirExitBiralatCancel() {
                    dismissDialog();
                    actionBar.selectTab(actionBar.getTabAt(2));
                }
            });
            dialog.show(ft, "exit");
        } else {
            finish();
        }
    }

    private void reloadData() {
        egyedList = app.getEgyedListByTENAZArray(selectedTenazArray);
        biraltEgyedList = new ArrayList<Egyed>();
        biralandoEgyedList = new ArrayList<Egyed>();

        if (egyedList.isEmpty()) {
            Log.i(LogUtil.TAG, "Üres az egyedlista!");
        } else {
            List<Biralat> biralatList = app.getBiralatListByTENAZArray(selectedTenazArray);
            HashMap<String, ArrayList<Biralat>> biralatMap = new HashMap<String, ArrayList<Biralat>>();
            for (Biralat biralat : biralatList) {
                String azono = biralat.getAZONO();
                if (biralatMap.get(azono) == null) {
                    biralatMap.put(azono, new ArrayList<Biralat>());
                }
                biralatMap.get(azono).add(biralat);
            }

            ArrayList<Egyed> biralandoEgyedList_KU = new ArrayList<Egyed>();
            Map<String, Egyed> biraltEgyedMap = new HashMap<String, Egyed>();
            List<Biralat> biraltEgyedBiralatList = new ArrayList<Biralat>();

            for (int i = 0; i < egyedList.size(); i++) {
                Egyed egyed = egyedList.get(i);
                List<Biralat> currentBiralatList = biralatMap.get(egyed.getAZONO());
                if (currentBiralatList == null) {
                    currentBiralatList = new ArrayList<Biralat>();
                }
                egyed.setBiralatList(currentBiralatList);

                Boolean biralt = false;
                for (Biralat biralat : egyed.getBiralatList()) {
                    if (biralat.getFELTOLTETLEN()) {
                        biraltEgyedMap.put(egyed.getAZONO(), egyed);
                        biraltEgyedBiralatList.add(biralat);
                        biralt = true;
                        break;
                    }
                }
                if (egyed.getKIVALASZTOTT() && !biralt) {
                    if ("HU".equals(egyed.getORSKO())) {
                        biralandoEgyedList.add(egyed);
                    } else {
                        biralandoEgyedList_KU.add(egyed);
                    }
                }
            }

            Collections.sort(biraltEgyedBiralatList, new Comparator<Biralat>() {
                @Override
                public int compare(Biralat lhs, Biralat rhs) {
                    return -1 * Long.valueOf(lhs.getBIRDA().getTime()).compareTo(rhs.getBIRDA().getTime());
                }
            });
            for (Biralat biralat : biraltEgyedBiralatList) {
                Egyed egyed = biraltEgyedMap.get(biralat.getAZONO());
                biraltEgyedList.add(egyed);
            }

            Comparator<Egyed> enarComparator = new Comparator<Egyed>() {
                @Override
                public int compare(Egyed lhs, Egyed rhs) {
                    return lhs.getAZONO().compareTo(rhs.getAZONO());
                }
            };
            Collections.sort(biralandoEgyedList, enarComparator);
            Collections.sort(biralandoEgyedList_KU, enarComparator);
            biralandoEgyedList.addAll(biralandoEgyedList_KU);
        }
    }

    private void updateHasznalatiSzamView(String azono, Boolean _hu) {
        if (azono.length() <= 4) {
            hasznalatiInput.setText(azono);
        } else if (!_hu || azono.length() != 10) {
            hasznalatiInput.setText(azono.substring(0, 4));
        } else {
            hasznalatiInput.setText(azono.substring(5, 9));
        }
    }

    public void onKeresoFragmentResume() {
        keresoFragment = adapter.getKeresoFragment();
        hasznalatiInput = (NumPadInput) adapter.getKeresoFragment().getView().findViewById(R.id.bir_hasznalatiInput);
        NumPad numpad = (NumPad) adapter.getKeresoFragment().getView().findViewById(R.id.bir_ker_numpad);
        numpad.setNumPadInput(hasznalatiInput);
        keresoFragment.updateKeresoButtons(egyedList);
        changeHUSelection(selectedEgyed);
        keresoFragment.updateDetails(selectedEgyed);
    }

    public void onBiralFragmentResume() {
        biralFragment = adapter.getBiralFragment();
    }

    public List<Egyed> filterByHasznalati(Boolean hu, String hasznalatiSzamString) {
        List<Egyed> foundList = new ArrayList<Egyed>();
        for (Egyed egyed : egyedList) {
            String ENAR = String.valueOf(egyed.getAZONO());
            if (hu && egyed.getORSKO().equals("HU") && ENAR.length() < 10 && ENAR.contains(hasznalatiSzamString)) {
                foundList.add(egyed);
            } else if (hu && egyed.getORSKO().equals("HU") && ENAR.length() == 10 && ENAR.substring(5, 9).equals(hasznalatiSzamString)) {
                foundList.add(egyed);
            } else if (!hu && !egyed.getORSKO().equals("HU") && ENAR.contains(hasznalatiSzamString)) {
                foundList.add(egyed);
            }
        }
        return foundList;
    }

    private void changeHUSelection(boolean isHU) {
        if (!hu.equals(isHU)) {
            hu = isHU;
            keresoFragment.updateHURadio(hu);
        }
    }

    private void changeHUSelection(Egyed egyed) {
        boolean isHu = true;
        if (egyed != null) {
            isHu = "HU".equals(egyed.getORSKO());
        }
        changeHUSelection(isHu);
    }

    public void HUClicked(View view) {
        boolean isHu = (view.getId() == R.id.bir_ker_check_hu);
        changeHUSelection(isHu);
    }

    public void selectHasznalatiInput(View view) {
        hasznalatiInput.invertSelection();
    }

    public void keres(View view) {
        String hasznalatiSzamValue = hasznalatiInput.getText().toString();
        if (hasznalatiSzamValue == null || hasznalatiSzamValue.isEmpty()) {
            return;
        }

        hasznalatiInput.select();
        if (biralFragment.getBiralatStarted()) {
            FragmentTransaction ft = getFragmentTransactionWithTag("unsaved");
            dialog = BirBirUnsavedBiralatDialog.newInstance(new BirBirUnsavedBiralatListener() {

                @Override
                public void onBirBirUnsavedBiralatCancel() {
                    dismissDialog();
                    if (selectedEgyed != null) {
                        updateHasznalatiSzamView(selectedEgyed.getAZONO(), hu);
                    }
                }

                @Override
                public void onBirBirUnsavedBiralatOk() {
                    dismissDialog();
                    biralFragment.clearCurrentBiralat();
                    keres(null);
                }
            });
            dialog.show(ft, "unsaved");
            return;
        }

        if (hu && hasznalatiSzamValue.length() < 4) {
            while (hasznalatiSzamValue.length() < 4) {
                hasznalatiSzamValue = "0" + hasznalatiSzamValue;
            }
            hasznalatiInput.setText(hasznalatiSzamValue);
        }
        List<Egyed> foundList = filterByHasznalati(hu, hasznalatiSzamValue);
        if (foundList.size() == 1) {
            Egyed egyed = foundList.get(0);
            onSingleSelect(egyed);
            if (!"HU".equals(egyed.getORSKO())) {
                hasznalatiInput.setText(hasznalatiSzamValue);
            }
        } else if (foundList.size() > 1) {
            selectedEgyed = null;
            FragmentTransaction ft = getFragmentTransactionWithTag("multi");
            dialog = BirKerMultiDialog.newInstance(new BirKerMultiListener() {
                @Override
                public void onSelect(Egyed egyed) {
                    dismissDialog();
                    onSingleSelect(egyed);
                    if (!"HU".equals(egyed.getORSKO())) {
                        hasznalatiInput.setText("----");
                    }
                }

                @Override
                public void onCancel() {
                    dismissDialog();
                }
            }, foundList);
            dialog.show(ft, "multi");
        } else {
            selectedEgyed = null;
            FragmentTransaction ft = getFragmentTransactionWithTag("notfound");
            dialog = BirKerNotfoundDialog.newInstance(this, selectedTenazArray, hasznalatiSzamValue);
            dialog.show(ft, "notfound");
        }
        changeHUSelection(selectedEgyed);
        keresoFragment.updateDetails(selectedEgyed);
    }

    @Override
    public void onAddNewEgyed(String tenaz, String orsko, String azono) {
        Egyed egyed = new Egyed();
        if (orsko.equals("HU") && tenaz.length() < 4) {
            while (tenaz.length() < 4) {
                tenaz = "0" + tenaz;
            }
        }
        egyed.setTENAZ(tenaz);
        egyed.setORSKO(orsko);
        egyed.setAZONO(azono);
        egyed.setKIVALASZTOTT(false);
        egyed.setUJ(true);
        app.insertEgyed(egyed);
        reloadData();
        selectedEgyed = findEgyedByAzono(azono);
        keresoFragment.updateKeresoButtons(egyedList);
        changeHUSelection(selectedEgyed);
        keresoFragment.updateDetails(selectedEgyed);
        updateHasznalatiSzamView(selectedEgyed.getAZONO(), hu);
        biralFragment.updateFragmentWithEgyed(selectedEgyed);
        dismissDialog();

    }

    private Egyed findEgyedByAzono(String azono) {
        for (Egyed egyed : egyedList) {
            if (egyed.getAZONO().equals(azono)) {
                return egyed;
            }
        }
        return null;
    }

    private void onSingleSelect(Egyed egyed) {
        String azono = String.valueOf(egyed.getAZONO());
        updateHasznalatiSzamView(azono, "HU".equals(egyed.getORSKO()));
        dismissDialog();

        if (!egyed.getKIVALASZTOTT()) {
            FragmentTransaction ft = getFragmentTransactionWithTag("BirKerNotSelectedDialog");
            dialog = BirKerNotSelectedDialog.newInstance(new BirKerNotselectedListener() {
                @Override
                public void onBiralSelected(Egyed egyed) {
                    dismissDialog();
                    selectedEgyed = egyed;
                    updateUIWithSelectedEgyed();
                }

                @Override
                public void onCancelSelection() {
                    dismissDialog();
                    if (selectedEgyed != null) {
                        updateHasznalatiSzamView(selectedEgyed.getAZONO(), hu);
                    }
                }
            }, egyed);
            dialog.show(ft, "BirKerNotSelectedDialog");
        } else {
            selectedEgyed = egyed;
            updateUIWithSelectedEgyed();
        }
    }

    private boolean isWithin30Days(Date date) {
        if (date != null && date.getTime() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.DAY_OF_YEAR, -30);
            Date maxDate = cal.getTime();
            if (maxDate.before(date)) {
                return true;
            }
        }
        return false;
    }

    private void updateUIWithSelectedEgyed() {
        changeHUSelection(selectedEgyed);
        keresoFragment.updateDetails(selectedEgyed);
        if (isWithin30Days(selectedEgyed.getELLDA())) {
            FragmentTransaction ft = getFragmentTransactionWithTag("ellda");
            dialog = NotificationDialog.newInstance(getString(R.string.bir_bir_dialog_ellda_title), null);
            dialog.show(ft, "ellda");
            biralFragment.updateFragmentWithEgyed(selectedEgyed, false);
        } else {
            Date lastBiralatDate = null;
            for (Biralat biralat : selectedEgyed.getBiralatList()) {
                if (biralat.getBIRDA() != null && biralat.getEXPORTALT() && (lastBiralatDate == null || lastBiralatDate.before(biralat.getBIRDA()))) {
                    lastBiralatDate = biralat.getBIRDA();
                }
            }
            if (isWithin30Days(lastBiralatDate)) {
                FragmentTransaction ft = getFragmentTransactionWithTag("lastBir");
                dialog = NotificationDialog.newInstance(getString(R.string.bir_bir_dialog_lastbir_title), null);
                dialog.show(ft, "lastBir");
                biralFragment.updateFragmentWithEgyed(selectedEgyed, false);
            } else {
                biralFragment.updateFragmentWithEgyed(selectedEgyed);
            }
        }
    }

    public void biral(View view) {
        actionBar.selectTab(actionBar.getTabAt(2));
    }

    public void showBiraltList(View view) {
        if (!biraltEgyedList.isEmpty() && !showingEgyedListDialog) {
            showingEgyedListDialog = true;
            FragmentTransaction ft = getFragmentTransactionWithTag("biralt");
            dialog = BirKerEgyedListDialog.newInstance(biraltEgyedList, true, new BirKerEgyedListDialog.EgyedClickListener() {
                @Override
                public void onEgyedClick(Egyed egyed) {
                    updateHasznalatiSzamView(egyed.getAZONO(), "HU".equals(egyed.getORSKO()));
                    onSingleSelect(egyed);
                }
            }, this);
            dialog.show(ft, "biralt");
        }
    }

    public void showBiralandoList(View view) {
        if (!biralandoEgyedList.isEmpty() && !showingEgyedListDialog) {
            showingEgyedListDialog = true;
            FragmentTransaction ft = getFragmentTransactionWithTag("biralando");
            dialog = BirKerEgyedListDialog.newInstance(biralandoEgyedList, true, new BirKerEgyedListDialog.EgyedClickListener() {
                @Override
                public void onEgyedClick(Egyed egyed) {
                    updateHasznalatiSzamView(egyed.getAZONO(), "HU".equals(egyed.getORSKO()));
                    onSingleSelect(egyed);
                }
            }, this);
            dialog.show(ft, "biralando");
        }
    }

    @Override
    public void onDismissEgyedListDialog() {
        showingEgyedListDialog = false;
    }

    // BÍRÁLAT

    public void onSaveBiralatClicked(View view) {
        if (selectedEgyed != null && biralFragment.getBiralatStarted()) {
            Biralat biralat = new Biralat();
            if (selectedBiralat != null) {
                biralat.setId(selectedBiralat.getId());
            }
            biralat.setTENAZ(selectedEgyed.getTENAZ());
            biralat.setAZONO(selectedEgyed.getAZONO());
            biralat.setFELTOLTETLEN(true);
            biralat.setEXPORTALT(false);
            biralat.setLETOLTOTT(false);
            biralat.setMEGJEGYZES(biralFragment.getMegjegyzes());
            biralat.setORSKO(selectedEgyed.getORSKO());
            biralat.setKULAZ(app.getBiraloAzonosito());
            biralat.setBIRDA(new Date());
            biralat.setBIRTI(7);

            String akakoString = biralFragment.getAkako();
            Map<String, String> map = biralFragment.getKodErtMap();
            if (akakoString == null || akakoString.isEmpty() || akakoString.equals("3")) {
                if (map != null) {
                    String invalidErtAtKod = invalidErtAtKod();
                    if (invalidErtAtKod != null) {
                        Toast.makeText(this, "Érvénytelen bírálat!", Toast.LENGTH_LONG).show();
                        biralFragment.forceEditing();
                        biralFragment.selectInputByKod(invalidErtAtKod);
                    } else {
                        if (akakoString != null && !akakoString.isEmpty() && akakoString.equals("3")) {
                            biralat.setAKAKO(Integer.valueOf(akakoString));
                        }
                        biralat.setKodErtMap(map);
                        saveBiralat(biralat);
                        biralFragment.updateFragmentWithEgyed(selectedEgyed);
                    }
                } else {
                    FragmentTransaction ft = getFragmentTransactionWithTag("unfinished");
                    dialog = BirBirUnfinishedBiralatDialog.newInstance(new BirBirUnfinishedBiralatListener() {
                        @Override
                        public void onBirBirUnfinishedBiralatCancel() {
                            dismissDialog();
                        }

                        @Override
                        public void onBirBirUnfinishedBiralatOk() {
                            biralFragment.clearCurrentBiralat();
                            actionBar.selectTab(actionBar.getTabAt(1));
                            dismissDialog();
                        }
                    });
                    dialog.show(ft, "unfinished");
                }
            } else {
                Integer akakoInt = Integer.valueOf(akakoString);
                if (akakoInt > 0 && akakoInt < 6) {
                    biralat.setAKAKO(akakoInt);
                    saveBiralat(biralat);
                    biralFragment.updateFragmentWithEgyed(selectedEgyed);
                }
            }
        }
    }

    private String invalidErtAtKod() {
        return biralFragment.invalidErtAtKod();
    }

    public void saveBiralat(Biralat biralat) {
        app.updateBiralat(biralat);
        biralFragment.setBiralatSaved();
        String azono = selectedEgyed.getAZONO();
        reloadData();
        selectedEgyed = findEgyedByAzono(azono);
        keresoFragment.updateKeresoButtons(egyedList);
        changeHUSelection(selectedEgyed);
        keresoFragment.updateDetails(selectedEgyed);
        actionBar.selectTab(actionBar.getTabAt(1));
    }

    public void onAkako(final String text) {
        if (text.equals("3")) {
            biralFragment.updateCurrentBiralatWithAkako(text);
        } else {
            FragmentTransaction ft = getFragmentTransactionWithTag("biralando");
            dialog = BirBirAkakoDialog.newInstance(new BirBirAkakoDialog.BirBirAkakoDialogListener() {
                @Override
                public void onNoClicked() {
                    biralFragment.updateCurrentBiralatWithAkako(null);
                    dismissDialog();
                }

                @Override
                public void onYesClicked() {
                    biralFragment.updateCurrentBiralatWithAkako(text);
                    dismissDialog();
                }
            });
            dialog.show(ft, "biralando");
        }
    }

    public void selectBiralat(Biralat lastBiralat) {
        selectedBiralat = lastBiralat;
    }

    public void openMegjegyzesDialog(String megjegyzes) {
        FragmentTransaction ft = getFragmentTransactionWithTag("megjegyzes");
        dialog = BirBirMegjegyzesDialog.newInstance(new BirBirMegjegyzesDialog.BirBirMegjegyzesListener() {
            @Override
            public void onBirBirBirBirMegjegyzesCancel(EditText et) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                dismissDialog();
            }

            @Override
            public void onBirBirBirBirMegjegyzesOk(EditText et, String megjegyzes) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                biralFragment.setMegjegyzes(megjegyzes);
                dismissDialog();
            }
        }, megjegyzes);
        dialog.show(ft, "megjegyzes");
    }
}
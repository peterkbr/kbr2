package hu.flexisys.kbr.view.biralat;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
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
public class BiralatActivity extends KbrActivity implements BirKerNotfoundListener, BirKerMultiListener {

    private static final String TAG = "KBR_BiralatActivity";

    private String[] selectedTenazArray;
    private BiralatPagerAdapter adapter;
    private NumPadInput hasznalatiInput;
    private List<Egyed> egyedList;
    private List<Egyed> biraltEgyedList;
    private List<Egyed> biralandoEgyedList;
    private Egyed selectedEgyed;
    private Boolean hu;
    private KeresoFragment keresoFragment;
    private BiralFragment biralFragment;

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
        String akakoString = biralFragment.getAkako();
        Map<String, String> map = biralFragment.getKodErtMap();
        if ((akakoString == null || akakoString.isEmpty() || akakoString.equals("3")) && map == null) {
            FragmentTransaction ft = getFragmentTransactionWithTag("unfinished");
            dialog = BirBirUnfinishedBiralatDialog.newInstance(new BirBirUnfinishedBiralatListener() {
                @Override
                public void onBirBirUnfinishedBiralatCancel() {
                    dismissDialog();
                }

                @Override
                public void onBirBirUnfinishedBiralatOk() {
                    dismissDialog();
                    finish();
                }
            });
            dialog.show(ft, "unfinished");
        } else {
            finish();
        }
    }

    private void reloadData() {
        egyedList = app.getEgyedListByTENAZArray(selectedTenazArray);
        biraltEgyedList = new ArrayList<Egyed>();
        biralandoEgyedList = new ArrayList<Egyed>();

        if (egyedList.isEmpty()) {
            Log.i(TAG, "Üres az egyedlista!");
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
                        biraltEgyedList.add(egyed);
                        biralt = true;
                        break;
                    }
                }
                if (egyed.getKIVALASZTOTT() && !biralt) {
                    biralandoEgyedList.add(egyed);
                }
            }
        }
    }

    private void updateHasznalatiSzamView(String azono) {
        if (azono.length() <= 4 || !hu) {
            hasznalatiInput.setText(azono);
        } else if (azono.length() == 10) {
            hasznalatiInput.setText(azono.substring(5, 9));
        } else {
            hasznalatiInput.setText(azono.substring(0, 4));
        }
    }

    public void onKeresoFragmentResume() {
        keresoFragment = adapter.getKeresoFragment();
        keresoFragment.updateHURadio(hu);
        hasznalatiInput = (NumPadInput) adapter.getKeresoFragment().getView().findViewById(R.id.bir_hasznalatiInput);
        NumPad numpad = (NumPad) adapter.getKeresoFragment().getView().findViewById(R.id.bir_ker_numpad);
        numpad.setNumPadInput(hasznalatiInput);
        keresoFragment.updateKeresoButtons(egyedList);
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

    public void huClicked(View view) {
        if (view.getId() == R.id.bir_ker_check_hu && !hu) {
            hu = true;
            keresoFragment.updateHURadio(hu);
        } else if (view.getId() == R.id.bir_ker_check_ku && hu) {
            hu = false;
            keresoFragment.updateHURadio(hu);
        }
    }

    public void selectHasznalatiInput(View view) {
        hasznalatiInput.invertSelection();
    }

    public void keres(View view) {
        hasznalatiInput.select();
        if (biralFragment.getBiralatStarted()) {
            FragmentTransaction ft = getFragmentTransactionWithTag("unsaved");
            dialog = BirBirUnsavedBiralatDialog.newInstance(new BirBirUnsavedBiralatListener() {

                @Override
                public void onBirBirUnsavedBiralatCancel() {
                    if (selectedEgyed != null) {
                        updateHasznalatiSzamView(selectedEgyed.getAZONO());
                    }
                    dismissDialog();
                }

                @Override
                public void onBirBirUnsavedBiralatOk() {
                    biralFragment.clearCurrentBiralat();
                    keres(null);
                }
            });
            dialog.show(ft, "unsaved");
            return;
        }

        String hasznalatiSzamValue = hasznalatiInput.getText().toString();
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
        } else if (foundList.size() > 1) {
            selectedEgyed = null;
            FragmentTransaction ft = getFragmentTransactionWithTag("multi");
            dialog = BirKerMultiDialog.newInstance(this, foundList);
            dialog.show(ft, "multi");
        } else {
            selectedEgyed = null;
            FragmentTransaction ft = getFragmentTransactionWithTag("notfound");
            dialog = BirKerNotfoundDialog.newInstance(this, selectedTenazArray, hasznalatiSzamValue);
            dialog.show(ft, "notfound");
        }
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
        updateHasznalatiSzamView(selectedEgyed.getAZONO());
        keresoFragment.updateKeresoButtons(egyedList);
        keresoFragment.updateDetails(selectedEgyed);
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

    @Override
    public void onSelect(Egyed egyed) {
        onSingleSelect(egyed);
    }

    private void onSingleSelect(Egyed egyed) {
        String azono = String.valueOf(egyed.getAZONO());
        updateHasznalatiSzamView(azono);
        dismissDialog();

        if (!egyed.getKIVALASZTOTT()) {
            FragmentTransaction ft = getFragmentTransactionWithTag("BirKerNotSelectedDialog");
            dialog = BirKerNotSelectedDialog.newInstance(new BirKerNotselectedListener() {
                @Override
                public void onBiralSelected(Egyed egyed) {
                    selectedEgyed = egyed;
                    updateUIWithSelectedEgyed();
                    dismissDialog();
                }

                @Override
                public void onCancelSelection() {
                    if (selectedEgyed != null) {
                        updateHasznalatiSzamView(selectedEgyed.getAZONO());
                    }
                    dismissDialog();
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
        keresoFragment.updateDetails(selectedEgyed);
        if (isWithin30Days(selectedEgyed.getELLDA())) {
            FragmentTransaction ft = getFragmentTransactionWithTag("ellda");
            dialog = NotificationDialog.newInstance(getString(R.string.bir_bir_dialog_ellda_title), null);
            dialog.show(ft, "ellda");
            biralFragment.updateFragmentWithEgyed(selectedEgyed, false);
        } else {
            Date lastBiralatDate = null;
            for (Biralat biralat : selectedEgyed.getBiralatList()) {
                if (biralat.getBIRDA() != null && !biralat.getFELTOLTETLEN() && (lastBiralatDate == null || lastBiralatDate.before(biralat.getBIRDA()))) {
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
        if (!biraltEgyedList.isEmpty()) {
            FragmentTransaction ft = getFragmentTransactionWithTag("biralt");
            dialog = BirKerEgyedListDialog.newInstance(biraltEgyedList);
            dialog.show(ft, "biralt");
        }
    }

    public void showBiralandoList(View view) {
        if (!biralandoEgyedList.isEmpty()) {
            FragmentTransaction ft = getFragmentTransactionWithTag("biralando");
            dialog = BirKerEgyedListDialog.newInstance(biralandoEgyedList);
            dialog.show(ft, "biralando");
        }
    }

    // BÍRÁLAT

    public void selectBiralatInput(View inputView) {
        biralFragment.selectInput(biralFragment.getView(), inputView);
    }

    public void onSaveBiralatClicked(View view) {
        if (selectedEgyed != null && biralFragment.getBiralatStarted()) {
            Biralat biralat = new Biralat();
            biralat.setTENAZ(selectedEgyed.getTENAZ());
            biralat.setAZONO(selectedEgyed.getAZONO());
            biralat.setFELTOLTETLEN(true);
            biralat.setEXPORTALT(false);
            biralat.setORSKO(selectedEgyed.getORSKO());
            biralat.setKULAZ(app.getKulaz());
            biralat.setBIRDA(new Date());
            biralat.setBIRTI(7);

            String akakoString = biralFragment.getAkako();
            Map<String, String> map = biralFragment.getKodErtMap();
            if (akakoString == null || akakoString.isEmpty() || akakoString.equals("3")) {
                if (map != null) {
                    if (akakoString != null && !akakoString.isEmpty() && akakoString.equals("3")) {
                        biralat.setAKAKO(Integer.valueOf(akakoString));
                    }
                    biralat.setKodErtMap(map);
                    saveBiralat(biralat);
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
                }
            }
        }
    }

    public void saveBiralat(Biralat biralat) {
        biralFragment.setBiralatSaved();
        app.updateBiralat(biralat);
        reloadData();
        selectedEgyed = findEgyedByAzono(selectedEgyed.getAZONO());
        keresoFragment.updateKeresoButtons(egyedList);
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
                    biralFragment.clearAkakoView();
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
}
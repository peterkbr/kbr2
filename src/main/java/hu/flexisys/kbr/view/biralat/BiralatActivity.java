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
import hu.flexisys.kbr.view.biralat.kereso.*;
import hu.flexisys.kbr.view.numpad.NumPad;
import hu.flexisys.kbr.view.numpad.NumPadInput;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class BiralatActivity extends KbrActivity implements BirKerNotfoundListener, BirKerMultiListener {

    private static final String TAG = "KBR_BiralatActivity";

    private String[] selectedTenazArray;
    private ViewPager pager;
    private BiralatPagerAdapter adapter;
    private NumPadInput hasznalatiInput;
    private List<Egyed> egyedList;
    private List<Egyed> biraltEgyedList;
    private List<Egyed> biralandoEgyedList;
    private Egyed selectedEgyedForKereso;
    private Egyed selectedEgyedForBiral;
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

        pager = (ViewPager) findViewById(R.id.biralat_pager);
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

    public void onKeresoFragmentResume() {
        keresoFragment = adapter.getKeresoFragment();
        keresoFragment.updateHURadio(hu);
        hasznalatiInput = (NumPadInput) adapter.getKeresoFragment().getView().findViewById(R.id.bir_hasznalatiInput);
        NumPad numpad = (NumPad) adapter.getKeresoFragment().getView().findViewById(R.id.bir_ker_numpad);
        numpad.setNumPadInput(hasznalatiInput);
        keresoFragment.updateKeresoButtons(egyedList);
        keresoFragment.updateDetails(selectedEgyedForKereso);
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
        String hasznalatiSzamValue = hasznalatiInput.getText().toString();
        if (hu && hasznalatiSzamValue.length() < 4) {
            while (hasznalatiSzamValue.length() < 4) {
                hasznalatiSzamValue = "0" + hasznalatiSzamValue;
            }
            hasznalatiInput.setText(hasznalatiSzamValue);
        }
        List<Egyed> foundList = filterByHasznalati(hu, hasznalatiSzamValue);
        if (foundList.size() == 1) {
            selectedEgyedForKereso = foundList.get(0);
        } else if (foundList.size() > 1) {
            selectedEgyedForKereso = null;
            FragmentTransaction ft = getFragmentTransactionWithTag("multi");
            dialog = BirKerMultiDialog.newInstance(this, foundList);
            dialog.show(ft, "multi");
        } else {
            selectedEgyedForKereso = null;
            FragmentTransaction ft = getFragmentTransactionWithTag("notfound");
            dialog = BirKerNotfoundDialog.newInstance(this, selectedTenazArray, hasznalatiSzamValue);
            dialog.show(ft, "notfound");
        }
        keresoFragment.updateDetails(selectedEgyedForKereso);
    }

    public void biral(View view) {
        if (selectedEgyedForKereso != null) {
            selectedEgyedForBiral = selectedEgyedForKereso;
            biralFragment.updateDetails(selectedEgyedForBiral);
            actionBar.selectTab(actionBar.getTabAt(2));
        }
    }

    @Override
    public void onAddNewEgyed(String tenaz, String orsko, String azono) {
        if (azono.length() <= 4) {
            hasznalatiInput.setText(azono);
        } else if (azono.length() == 10) {
            hasznalatiInput.setText(azono.substring(5, 9));
        } else {
            hasznalatiInput.setText(azono.substring(0, 4));
        }

        Egyed egyed = new Egyed();
        egyed.setTENAZ(tenaz);
        egyed.setORSKO(orsko);
        egyed.setAZONO(azono);
        egyed.setKIVALASZTOTT(false);
        egyed.setUJ(true);
        app.insertEgyed(egyed);

        reloadData();
        keresoFragment.updateKeresoButtons(egyedList);
        keresoFragment.updateDetails(selectedEgyedForKereso);
        dismissDialog();

        // TODO hmm
        keres(null);
    }

    @Override
    public void onSelect(Egyed egyed) {
        String azono = String.valueOf(egyed.getAZONO());
        if (azono.length() <= 4) {
            hasznalatiInput.setText(azono);
        } else if (azono.length() == 10) {
            hasznalatiInput.setText(azono.substring(5, 9));
        } else {
            hasznalatiInput.setText(azono.substring(0, 4));
        }
        selectedEgyedForKereso = egyed;
        keresoFragment.updateDetails(selectedEgyedForKereso);
        dismissDialog();
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

    public void selectBiralatInput(View view) {
        biralFragment.selectInput(view);
    }

    public void saveBiralat(View view) {
        if (selectedEgyedForBiral != null) {
            Biralat biralat = new Biralat();
            biralat.setTENAZ(selectedEgyedForBiral.getTENAZ());
            biralat.setAZONO(selectedEgyedForBiral.getAZONO());
            biralat.setFELTOLTETLEN(true);
            biralat.setORSKO(selectedEgyedForBiral.getORSKO());
            biralat.setKULAZ(app.getUserId());
            biralat.setBIRDA(new Date());
            app.insertBiralat(biralat);

            reloadData();
            for (Egyed egyed : egyedList) {
                if (egyed.getAZONO().equals(selectedEgyedForKereso.getAZONO())) {
                    selectedEgyedForKereso = egyed;
                }
            }
            keresoFragment.updateKeresoButtons(egyedList);
            keresoFragment.updateDetails(selectedEgyedForKereso);
            actionBar.selectTab(actionBar.getTabAt(1));
        }
    }
}
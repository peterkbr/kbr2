package hu.flexisys.kbr.view.biralat;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.*;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.biralat.kereso.*;
import hu.flexisys.kbr.view.numpad.NumPad;
import hu.flexisys.kbr.view.numpad.NumPadInput;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class BiralatActivity extends KbrActivity implements BirKerNotfoundListener, BirKerMultiListener {

    private static final String TAG = "KBR_BiralatActivity";

    private long[] selectedTenazArray;
    private ViewPager pager;
    private BiralatPagerAdapter adapter;
    private NumPadInput hasznalatiInput;
    private List<Egyed> egyedList;
    private List<Egyed> biraltEgyedList;
    private List<Egyed> biralandoEgyedList;
    private Egyed selectedEgyed;
    private Boolean hu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.activity_biralat);

        actionBar.setDisplayShowHomeEnabled(false);
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

        selectedTenazArray = getIntent().getExtras().getLongArray(BiralatTenyeszetActivity.EXTRAKEY_SELECTEDTENAZLIST);
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
            for (int i = 0; i < egyedList.size(); i++) {
                Egyed egyed = egyedList.get(i);
                if (!egyed.getBiralatList().isEmpty()) {
                    biraltEgyedList.add(egyed);
                } else if (egyed.getKIVALASZTOTT()) {
                    biralandoEgyedList.add(egyed);
                }
            }
        }
    }

    public void onKeresoFragmentResume() {
        updateHURadio();
        hasznalatiInput = (NumPadInput) adapter.getKeresoFragment().getView().findViewById(R.id.bir_hasznalatiInput);
        NumPad numpad = (NumPad) adapter.getKeresoFragment().getView().findViewById(R.id.bir_ker_numpad);
        numpad.setNumPadInput(hasznalatiInput);
        updateKeresoButtons();
        updateDetails();
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
            updateHURadio();
        } else if (view.getId() == R.id.bir_ker_check_ku && hu) {
            hu = false;
            updateHURadio();
        }
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
            selectedEgyed = foundList.get(0);
        } else if (foundList.size() > 1) {
            // TODO dialog
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
        updateDetails();
    }

    // UPDATE THE VIEWS

    private void updateHURadio() {
        View view = adapter.getKeresoFragment().getView();
        RadioButton radio = (RadioButton) view.findViewById(R.id.bir_ker_check_hu);
        radio.setChecked(hu);
        radio = (RadioButton) view.findViewById(R.id.bir_ker_check_ku);
        radio.setChecked(!hu);
    }

    private void updateKeresoButtons() {
        Integer biralt = 0;
        Integer biraltPlusz = 0;
        Integer biralando = 0;
        for (Egyed egyed : egyedList) {
            if (!egyed.getBiralatList().isEmpty()) {
                if (egyed.getKIVALASZTOTT()) {
                    ++biralt;
                } else {
                    ++biraltPlusz;
                }
            }
            if (egyed.getKIVALASZTOTT()) {
                ++biralando;
            }
        }
        biralando -= biralt;

        View view = adapter.getKeresoFragment().getView();
        Button button = (Button) view.findViewById(R.id.bir_ker_b1);
        button.setText(biralt + (biraltPlusz > 0 ? ("+" + biraltPlusz) : ""));
        button = (Button) view.findViewById(R.id.bir_ker_b2);
        button.setText(String.valueOf(biralando));
    }

    private void updateDetails() {
        View view = adapter.getKeresoFragment().getView();
        LinearLayout itvLayout = (LinearLayout) view.findViewById(R.id.bir_ker_itvLayout);
        LinearLayout detailsLayout = (LinearLayout) view.findViewById(R.id.bir_ker_details);
        if (selectedEgyed == null) {
            hu = true;
            updateHURadio();
            itvLayout.setVisibility(View.INVISIBLE);
            detailsLayout.setVisibility(View.INVISIBLE);
            TextView textView = (TextView) view.findViewById(R.id.bir_ker_enar);
            textView.setBackgroundColor(getResources().getColor(R.color.green));
            textView.setText("");
        } else {
            itvLayout.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.VISIBLE);

            hu = selectedEgyed.getORSKO().equals("HU");
            updateHURadio();

            CheckBox itvCheckBox = (CheckBox) view.findViewById(R.id.bir_ker_itvCheckBox);
            if (selectedEgyed.getITVJE() == null) {
                itvLayout.setVisibility(View.INVISIBLE);
            } else {
                itvCheckBox.setChecked(selectedEgyed.getITVJE());
            }

            TextView textView = (TextView) view.findViewById(R.id.bir_ker_enar);
            String text = String.valueOf(selectedEgyed.getAZONO());
            if (text.length() == 10) {
                Spanned spanned = Html.fromHtml(text.substring(0, 5) + " <b>" + text.substring(5, 9) + "</b> " + text.substring(9));
                textView.setText(spanned);
            } else {
                textView.setText(text);
            }
            if (selectedEgyed.getUJ()) {
                textView.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (!selectedEgyed.getBiralatList().isEmpty()) {
                textView.setBackgroundColor(getResources().getColor(R.color.blue));
            } else if (selectedEgyed.getKIVALASZTOTT()) {
                textView.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.gray));
            }

            textView = (TextView) view.findViewById(R.id.bir_ker_laktNapok);
            text = "0";
            if (selectedEgyed.getELLDA() != null && selectedEgyed.getELLDA().getTime() > 1) {
                long diff = (new Date().getTime() - selectedEgyed.getELLDA().getTime()) / (1000 * 60 * 60 * 24);
                int diffInDays = Math.round(diff);
                text = String.valueOf(diffInDays);
            }
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_laktSzam);
            text = "-";
            if (selectedEgyed.getELLSO() != null) {
                text = String.valueOf(selectedEgyed.getELLSO());
            }
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_ellesDatuma);
            text = "Nem ellett";
            if (selectedEgyed.getELLDA() != null && selectedEgyed.getELLDA().getTime() > 1) {
                text = DateUtil.formatDate(selectedEgyed.getELLDA());
            }
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_szuletes);
            text = "-";
            if (selectedEgyed.getSZULD() != null) {
                text = DateUtil.formatDate(selectedEgyed.getSZULD());
            }
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_konstrKod);
            text = "-";
            if (selectedEgyed.getKONSK() != null) {
                text = String.valueOf(selectedEgyed.getKONSK());
            }
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_szinkod);
            text = "-";
            if (selectedEgyed.getSZINE() != null) {
                text = String.valueOf(selectedEgyed.getSZINE());
            }
            textView.setText(text);
        }
    }

    @Override
    public void onAdd(String tenaz, String orsko, String azono) {
        if (azono.length() <= 4) {
            hasznalatiInput.setText(azono);
        } else if (azono.length() == 10) {
            hasznalatiInput.setText(azono.substring(5, 9));
        } else {
            hasznalatiInput.setText(azono.substring(0, 4));
        }

        Egyed egyed = new Egyed();
        egyed.setTENAZ(Long.valueOf(tenaz));
        egyed.setORSKO(orsko);
        egyed.setAZONO(Long.valueOf(azono));
        egyed.setKIVALASZTOTT(false);
        egyed.setUJ(true);
        app.insertEgyed(egyed);

        reloadData();
        updateKeresoButtons();
        updateDetails();
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
        selectedEgyed = egyed;
        updateDetails();
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
}
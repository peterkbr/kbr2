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
import hu.flexisys.kbr.view.biralat.kereso.BirKerNotfoundDialog;
import hu.flexisys.kbr.view.biralat.kereso.BirKerNotfoundListener;
import hu.flexisys.kbr.view.numpad.NumPadContainer;
import hu.flexisys.kbr.view.numpad.NumPadInput;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class BiralatActivity extends KbrActivity implements NumPadContainer, BirKerNotfoundListener {

    private static final String TAG = "KBR_BiralatActivity";

    private long[] selectedTenazArray;
    private ViewPager pager;
    private BiralatPagerAdapter adapter;
    private NumPadInput hasznalatiInput;
    private List<Egyed> egyedList;
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
        if (egyedList.isEmpty()) {
            Log.i(TAG, "Üres az egyedlista!");
        } else {
            for (int i = 0; i < (egyedList.size() > 5 ? 5 : egyedList.size()); i++) {
                Egyed egyed = egyedList.get(i);
                Log.i(TAG, egyed.getAZONO() + ":" + egyed.getORSKO());
            }
        }
    }

    public void onKeresoFragmentResume() {
        updateHURadio();
        hasznalatiInput = (NumPadInput) adapter.getKeresoFragment().getView().findViewById(R.id.bir_hasznalatiInput);
        updateKeresoButtons();
        updateDetails();
    }

    public List<Egyed> filterByHasznalati(Boolean hu, String hasznalatiSzamString) {
        List<Egyed> foundList = new ArrayList<Egyed>();
        for (Egyed egyed : egyedList) {
            String ENAR = String.valueOf(egyed.getAZONO());
            if (ENAR == null || ENAR.length() != 10) {
                continue;
            }
            if (hu && egyed.getORSKO().equals("HU") && ENAR.substring(5, 9).equals(hasznalatiSzamString)) {
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

    public void okClicked(View view) {
        String hasznalatiSzamValue = hasznalatiInput.getText().toString();
        if (hu && hasznalatiSzamValue.length() < 4) {
            while (hasznalatiSzamValue.length() < 4) {
                hasznalatiSzamValue = "0" + hasznalatiSzamValue;
            }
        }
        List<Egyed> foundList = filterByHasznalati(hu, hasznalatiSzamValue);
        if (foundList.size() == 1) {
            selectedEgyed = foundList.get(0);
        } else if (foundList.size() > 1) {
            // TODO dialog
            selectedEgyed = null;
            toast("Multiple match found!");
        } else {
            selectedEgyed = null;
            FragmentTransaction ft = getFragmentTransactionWithTag("notfound");
            dialog = BirKerNotfoundDialog.newInstance(this, selectedTenazArray);
            dialog.show(ft, "notfound");
        }
        updateDetails();
    }

    public void onNumPadClick(View view) {
        TextView tv = (TextView) view;
        String text = tv.getText().toString();
        onInput(Integer.valueOf(text));
    }

    @Override
    public void onInput(Integer num) {
        hasznalatiInput.onInput(num);
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
            itvLayout.setVisibility(View.INVISIBLE);
            detailsLayout.setVisibility(View.INVISIBLE);
            TextView textView = (TextView) view.findViewById(R.id.bir_ker_enar);
            textView.setBackgroundColor(getResources().getColor(R.color.green));
            textView.setText("");
        } else {
            itvLayout.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.VISIBLE);

            CheckBox itvCheckBox = (CheckBox) view.findViewById(R.id.bir_ker_itvCheckBox);
            itvCheckBox.setChecked(selectedEgyed.getITVJE());

            TextView textView = (TextView) view.findViewById(R.id.bir_ker_enar);
            String text = String.valueOf(selectedEgyed.getAZONO());
            Spanned spanned = Html.fromHtml(text.substring(0, 5) + " <b>" + text.substring(5, 9) + "</b> " + text.substring(9));
            textView.setText(spanned);
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
            text = String.valueOf(selectedEgyed.getELLSO());
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_ellesDatuma);
            text = "Nem ellett";
            if (selectedEgyed.getELLDA() != null && selectedEgyed.getELLDA().getTime() > 1) {
                text = DateUtil.formatDate(selectedEgyed.getELLDA());
            }
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_szuletes);
            text = DateUtil.formatDate(selectedEgyed.getSZULD());
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_konstrKod);
            text = String.valueOf(selectedEgyed.getKONSK());
            textView.setText(text);

            textView = (TextView) view.findViewById(R.id.bir_ker_szinkod);
            text = String.valueOf(selectedEgyed.getSZINE());
            textView.setText(text);
        }
    }

    @Override
    public void setNumPadInput(NumPadInput numPadInput) {
        hasznalatiInput = numPadInput;
    }

    @Override
    public void onAdd(String tenaz, String azon, String orsko) {
        hasznalatiInput = (NumPadInput) adapter.getKeresoFragment().getView().findViewById(R.id.bir_hasznalatiInput);
        dismissDialog();
        // TODO add egyed : uj = true
    }
}
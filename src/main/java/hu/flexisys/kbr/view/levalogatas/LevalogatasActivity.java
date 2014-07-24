package hu.flexisys.kbr.view.levalogatas;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.tenyeszet.LevalogatasTorlesAlertDialog;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertListener;

import java.util.*;

/**
 * Created by Peter on 2014.07.21..
 */
public class LevalogatasActivity extends KbrActivity implements OnSelectionChangedListener, SelectionChangedAlertListener, TorlesAlertListener {

    private static final String TAG = "KBR_LevalogatasActivity";
    private String[] selectedTenazArray;
    private List<Egyed> egyedList;
    private Integer selectedEgyedCounter;
    private LevalogatasListViewAdapter adapter;

    private SlidingPaneLayout pane;
    private Filter filter;
    private Boolean selectionChanged = false;
    private String currentOrderBy;
    private Boolean asc;

    // MENU IN ACTIONBAR

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levalogatas);

        View mLogoView = LayoutInflater.from(this).inflate(R.layout.activity_levalogatas_actionbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(mLogoView);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        filter = new Filter();

        selectedTenazArray = getIntent().getExtras().getStringArray(BiralatTenyeszetActivity.EXTRAKEY_SELECTEDTENAZLIST);
        egyedList = new ArrayList<Egyed>();
        reloadData();
        reorderData(getString(R.string.lev_grid_header_szuletett));

        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        pane.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelOpened(View panel) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelClosed(View panel) {
                invalidateOptionsMenu();
            }
        });

        TextView telep = (TextView) findViewById(R.id.lev_szuk_telep_name);
        List<Tenyeszet> tenyeszetList = app.getTenyeszetListByTENAZArray(selectedTenazArray);
        String text = tenyeszetList.size() + " db telep";
        if (tenyeszetList.size() == 1) {
            text = tenyeszetList.get(0).getTARTO();
        }
        telep.setText(text);

        updateCounters();

        CheckBox selectAll = (CheckBox) findViewById(R.id.lev_select_all);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (Egyed egyed : egyedList) {
                    egyed.setKIVALASZTOTT(isChecked);
                }
                onSelectionChanged();
                adapter.notifyDataSetChanged();
            }
        });

        ListView listView = (ListView) findViewById(R.id.teny_list);
        listView.setEmptyView(findViewById(R.id.empty_list_item));
        adapter = new LevalogatasListViewAdapter(this, R.layout.list_levalogatas, egyedList, this);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int resId = pane.isOpen() ? R.menu.menu_activity_levalogatas_szukites : R.menu.menu_activity_levalogatas;
        inflater.inflate(resId, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onHomeClicked();
                return true;
            case R.id.szukites:
                showSzukites();
                return true;
            case R.id.mentes_kilepes:
                mentes_kilepes();
                return true;
            case R.id.mentes:
                mentes();
                return true;
            case R.id.torles:
                torles();
                return true;
            case R.id.levalogatas_torles:
                levalogatasTorles();
                return true;
            case R.id.szukit:
                szukit();
                return true;
            case R.id.urit:
                urit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void onHomeClicked() {
        if (selectionChanged) {
            FragmentTransaction ft = getFragmentTransactionWithTag("selectionChangedDialog");
            dialog = SelectionChangedAlertDialog.newInstance(this);
            dialog.show(ft, "selectionChangedDialog");
        } else {
            finish();
        }
    }

    // onHomeClicked
    @Override
    public void onKilepes() {
        dismissDialog();
        finish();
    }

    // onHomeClicked
    @Override
    public void onMentesEsKilepes() {
        dismissDialog();
        mentes_kilepes();
    }

    private void reloadData() {
        egyedList.clear();
        List<Egyed> rawList = app.getEgyedListByTENAZArray(selectedTenazArray);
        for (Egyed egyed : rawList) {
            if (applyFilter(egyed)) {
                egyedList.add(egyed);
            }
        }
        selectedEgyedCounter = 0;

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
                if (egyed.getKIVALASZTOTT()) {
                    selectedEgyedCounter++;
                }
                List<Biralat> currentBiralatList = biralatMap.get(egyed.getAZONO());
                if (currentBiralatList == null) {
                    currentBiralatList = new ArrayList<Biralat>();
                }
                egyed.setBiralatList(currentBiralatList);
            }
        }
    }

    private void reorderData(String orderBy) {
        if (asc == null || currentOrderBy == null || !currentOrderBy.equals(orderBy)) {
            currentOrderBy = orderBy;
            asc = true;
        } else {
            asc = !asc;
        }

        final Comparator<Egyed> comparator = new Comparator<Egyed>() {
            @Override
            public int compare(Egyed leftEgyed, Egyed rightEgyed) {
                int value = 0;
                if (currentOrderBy.equals(getString(R.string.lev_grid_header_ok))) {
                    if (!leftEgyed.getORSKO().equals("HU") && rightEgyed.getORSKO().equals("HU")) {
                        value = -1;
                    } else if (leftEgyed.getORSKO().equals("HU") && !rightEgyed.getORSKO().equals("HU")) {
                        value = 1;
                    }
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_enar))) {
                    value = Long.valueOf(leftEgyed.getAZONO()).compareTo(Long.valueOf(rightEgyed.getAZONO()));
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_es))) {
                    value = leftEgyed.getELLSO().compareTo(rightEgyed.getELLSO());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_elles_datuma))) {
                    value = leftEgyed.getELLDA().compareTo(rightEgyed.getELLDA());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_szuletett))) {
                    value = leftEgyed.getSZULD().compareTo(rightEgyed.getSZULD());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_kon))) {
                    value = leftEgyed.getKONSK().compareTo(rightEgyed.getKONSK());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_itv))) {
                    value = leftEgyed.getITVJE().compareTo(rightEgyed.getITVJE());
                }
                if (!asc) {
                    value *= -1;
                }
                return value;
            }
        };

        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                Collections.sort(egyedList, comparator);
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        task.execute();
    }

    private boolean applyFilter(Egyed egyed) {
        String esFilter = (String) filter.get(Filter.ELLES_SORSZAMAI);
        if (esFilter != null && !esFilter.isEmpty()) {
            String[] esArray = esFilter.split(",");
            Boolean cool = false;
            for (String es : esArray) {
                if (Integer.valueOf(es).equals(egyed.getELLSO())) {
                    cool = true;
                    break;
                }
            }
            if (!cool) {
                return false;
            }
        }

        String enar = (String) filter.get(Filter.ENAR);
        if (enar != null && !enar.isEmpty()) {
            if (egyed.getAZONO() == null || !enar.equals(String.valueOf(egyed.getAZONO()))) {
                return false;
            }
        }
        return true;
    }

    private void updateCounters() {
        TextView counterView = (TextView) findViewById(R.id.lev_szuk_selected_egyed_counter);
        counterView.setText(String.valueOf(selectedEgyedCounter));
        counterView = (TextView) findViewById(R.id.lev_szuk_egyed_counter);
        counterView.setText(String.valueOf(egyedList.size()));
    }

    @Override
    public void onSelectionChanged() {
        selectionChanged = true;
    }


    public void levalogatasTorles() {
        FragmentTransaction ft = getFragmentTransactionWithTag("levalogatasTorlesDialog");
        dialog = LevalogatasTorlesAlertDialog.newInstance(this);
        dialog.show(ft, "levalogatasTorlesDialog");
    }

    // levalogatasTorlese
    @Override
    public void onTorles() {
        dismissDialog();
        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                app.removeSelectionFromTenyeszetList(selectedTenazArray);
                reloadData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        task.execute();
    }

    // LEVÁLOGATÁS
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    public void mentes_kilepes() {
        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                saveEgyedList();
                selectionChanged = false;
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                dismissDialog();
                finish();
            }
        });
        task.execute();
    }

    public void mentes() {
        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                saveEgyedList();
                filter.clear();
                reloadData();
                selectionChanged = false;
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                urit();
                dismissDialog();
            }
        });
        task.execute();
    }

    private void saveEgyedList() {
        for (Egyed egyed : egyedList) {
            app.updateEgyedWithSelection(egyed.getAZONO(), egyed.getKIVALASZTOTT());
        }
    }

    public void torles() {
        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                filter.clear();
                reloadData();
                selectionChanged = false;
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        task.execute();
    }

    public void reorder(View view) {
        TextView orderByTV = (TextView) view;
        String orderBy = orderByTV.getText().toString();
        reorderData(orderBy);
    }

    // SZŰKÍTÉS
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    public void szukit() {
        EditText enarET = (EditText) findViewById(R.id.lev_szuk_enar);
        String enarFilter = enarET.getText().toString();
        filter.put(Filter.ENAR, enarFilter);

        EditText esET = (EditText) findViewById(R.id.lev_szuk_elles_sorszamai);
        String esFilter = esET.getText().toString();
        filter.put(Filter.ELLES_SORSZAMAI, esFilter);

        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                reloadData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        task.execute();
    }

    public void urit() {
        clearCheckBox(R.id.lev_szuk_mar_ellett_chb);
        clearCheckBox(R.id.lev_szuk_nem_biralt_chb);
        clearCheckBox(R.id.lev_szuk_mar_ellett_chb);

        clearEditText(R.id.lev_szuk_elles_sorszamai);
        clearEditText(R.id.lev_szuk_enar);
        clearEditText(R.id.lev_szuk_utolso_elles_tol);
        clearEditText(R.id.lev_szuk_utolso_elles_ig);
        clearEditText(R.id.lev_szuk_szuletes_tol);
        clearEditText(R.id.lev_szuk_szuletes_ig);
        clearEditText(R.id.lev_szuk_konstrukcios_tol);
        clearEditText(R.id.lev_szuk_konstrukcios_ig);

        clearRadioButton(R.id.lev_szuk_hu_radio);
        clearRadioButton(R.id.lev_szuk_ku_radio);
    }

    private void clearCheckBox(int resId) {
        CheckBox checkBox = (CheckBox) findViewById(resId);
        checkBox.setChecked(false);
    }

    private void clearEditText(int resId) {
        EditText editText = (EditText) findViewById(resId);
        editText.setText("");
    }

    private void clearRadioButton(int resId) {
        RadioButton radioButton = (RadioButton) findViewById(resId);
        radioButton.setChecked(false);
    }

    public void showSzukites() {
        pane.openPane();
    }

}

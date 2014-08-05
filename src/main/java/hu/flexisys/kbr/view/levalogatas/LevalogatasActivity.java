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
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.tenyeszet.LevalogatasTorlesAlertDialog;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertListener;

import java.text.ParseException;
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

        final RadioButton huButton = (RadioButton) findViewById(R.id.lev_szuk_hu_radio);
        final RadioButton kuButton = (RadioButton) findViewById(R.id.lev_szuk_ku_radio);
        huButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    kuButton.setChecked(false);
                }
            }
        });
        kuButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    huButton.setChecked(false);
                }
            }
        });

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
        selectedEgyedCounter = 0;
        List<Egyed> rawList = app.getEgyedListByTENAZArray(selectedTenazArray);

        List<Biralat> biralatList = app.getBiralatListByTENAZArray(selectedTenazArray);
        HashMap<String, ArrayList<Biralat>> biralatMap = new HashMap<String, ArrayList<Biralat>>();
        for (Biralat biralat : biralatList) {
            String azono = biralat.getAZONO();
            if (biralatMap.get(azono) == null) {
                biralatMap.put(azono, new ArrayList<Biralat>());
            }
            biralatMap.get(azono).add(biralat);
        }

        for (Egyed egyed : rawList) {
            List<Biralat> currentBiralatList = biralatMap.get(egyed.getAZONO());
            if (currentBiralatList == null) {
                currentBiralatList = new ArrayList<Biralat>();
            }
            egyed.setBiralatList(currentBiralatList);
            if (applyFilter(egyed)) {
                egyedList.add(egyed);
                if (egyed.getKIVALASZTOTT()) {
                    selectedEgyedCounter++;
                }
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
                Log.i(TAG, "Reorder started:" + DateUtil.getRequestId());
                Collections.sort(egyedList, comparator);
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                adapter.notifyDataSetChanged();
                dismissDialog();
                Log.i(TAG, "Reorder finished:" + DateUtil.getRequestId());
            }
        });
        task.execute();
    }

    private boolean applyFilter(Egyed egyed) {
        Boolean marEllett = (Boolean) filter.get(Filter.MAR_ELLETT);
        if (marEllett != null && marEllett && (egyed.getELLDA() == null || egyed.getELLDA().getTime() <= 1)) {
            return false;
        }

        Boolean nemBiralt = (Boolean) filter.get(Filter.NEM_BIRALT);
        if (nemBiralt != null && nemBiralt && !egyed.getBiralatList().isEmpty()) {
            return false;
        }

        Boolean itv = (Boolean) filter.get(Filter.ITV);
        if (itv != null && itv && (egyed.getITVJE() == null || !egyed.getITVJE())) {
            return false;
        }

        Boolean hu = (Boolean) filter.get(Filter.HU);
        if (hu != null && ((hu && !egyed.getORSKO().equals("HU")) || (!hu && egyed.getORSKO().equals("HU")))) {
            return false;
        }

        String esFilter = (String) filter.get(Filter.ELLES_SORSZAMAI);
        if (esFilter != null && !esFilter.isEmpty()) {
            String[] esArray = esFilter.split(",");
            Boolean pass = false;
            for (String es : esArray) {
                if (Integer.valueOf(es).equals(egyed.getELLSO())) {
                    pass = true;
                    break;
                }
            }
            if (!pass) {
                return false;
            }
        }

        String enar = (String) filter.get(Filter.ENAR);
        if (enar != null && !enar.isEmpty() && (egyed.getAZONO() == null || !egyed.getAZONO().contains(String.valueOf(enar)))) {
            return false;
        }

        Long szuletesTol = (Long) filter.get(Filter.SZULETES_TOL);
        Long szuletesIg = (Long) filter.get(Filter.SZULETES_IG);
        Long szuletes = egyed.getSZULD().getTime();
        if ((szuletesTol != null && szuletes < szuletesTol) || (szuletesIg != null && szuletes > szuletesIg)) {
            return false;
        }

        Long utolsoEllesTol = (Long) filter.get(Filter.UTOLSO_ELLES_TOL);
        Long utolsoEllesIg = (Long) filter.get(Filter.UTOLSO_ELLES_IG);
        Long utolsoElles = egyed.getELLDA().getTime();
        if ((utolsoEllesTol != null && utolsoElles < utolsoEllesTol) || (utolsoEllesIg != null && utolsoElles > utolsoEllesIg)) {
            return false;
        }

        Integer konTol = (Integer) filter.get(Filter.KONSTRUKCIOS_TOL);
        Integer konIg = (Integer) filter.get(Filter.KONSTRUKCIOS_IG);
        Integer kon = egyed.getKONSK();
        if ((konTol != null && kon < konTol) || (konIg != null && kon > konIg)) {
            return false;
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
        startProgressDialog();
        updateFilterValues();
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

    private void updateFilterValues() {
        Boolean marEllett = getBooleanFromCheckBox(R.id.lev_szuk_mar_ellett_chb);
        filter.put(Filter.MAR_ELLETT, marEllett);

        Boolean nemBiralt = getBooleanFromCheckBox(R.id.lev_szuk_nem_biralt_chb);
        filter.put(Filter.NEM_BIRALT, nemBiralt);

        Boolean itv = getBooleanFromCheckBox(R.id.lev_szuk_itv_chb);
        filter.put(Filter.ITV, itv);

        RadioButton huRadioButton = (RadioButton) findViewById(R.id.lev_szuk_hu_radio);
        RadioButton kuRadioButton = (RadioButton) findViewById(R.id.lev_szuk_ku_radio);
        if (huRadioButton.isChecked()) {
            filter.put(Filter.HU, true);
        } else if (kuRadioButton.isChecked()) {
            filter.put(Filter.HU, false);
        } else {
            filter.put(Filter.HU, null);
        }

        String ellesSorszamai = getStringFromEditText(R.id.lev_szuk_elles_sorszamai);
        filter.put(Filter.ELLES_SORSZAMAI, ellesSorszamai);

        String enar = getStringFromEditText(R.id.lev_szuk_enar);
        filter.put(Filter.ENAR, enar);

        String konTol = getStringFromEditText(R.id.lev_szuk_konstrukcios_tol);
        filter.put(Filter.KONSTRUKCIOS_TOL, konTol == null ? null : Integer.valueOf(konTol));

        String konIg = getStringFromEditText(R.id.lev_szuk_konstrukcios_ig);
        filter.put(Filter.KONSTRUKCIOS_IG, konIg == null ? null : Integer.valueOf(konIg));

        Long ellesTol = getDateInLongFromTextView(R.id.lev_szuk_utolso_elles_tol);
        filter.put(Filter.UTOLSO_ELLES_TOL, ellesTol);

        Long ellesIg = getDateInLongFromTextView(R.id.lev_szuk_utolso_elles_ig);
        filter.put(Filter.UTOLSO_ELLES_IG, ellesIg);

        Long szuletesTol = getDateInLongFromTextView(R.id.lev_szuk_szuletes_tol);
        filter.put(Filter.SZULETES_TOL, szuletesTol);

        Long szuletesIg = getDateInLongFromTextView(R.id.lev_szuk_szuletes_ig);
        filter.put(Filter.SZULETES_IG, szuletesIg);
    }

    private Boolean getBooleanFromCheckBox(int resId) {
        CheckBox checkBox = (CheckBox) findViewById(resId);
        Boolean checkValue = checkBox.isChecked();
        if (!checkValue) {
            checkValue = null;
        }
        return checkValue;
    }

    private String getStringFromEditText(int resId) {
        EditText editText = (EditText) findViewById(resId);
        String filterValue = editText.getText().toString();
        if (filterValue == null || filterValue.isEmpty()) {
            filterValue = null;
        }
        return filterValue;
    }

    private Long getDateInLongFromTextView(int resId) {
        TextView textView = (TextView) findViewById(resId);
        String filterValue = String.valueOf(textView.getText());
        Long longValue = null;
        try {
            if (filterValue != null && !filterValue.isEmpty()) {
                longValue = DateUtil.getDateFromDateString(filterValue).getTime();
            }
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error", e);
        }
        return longValue;
    }

    public void urit() {
        clearCheckBox(R.id.lev_szuk_mar_ellett_chb);
        clearCheckBox(R.id.lev_szuk_nem_biralt_chb);
        clearCheckBox(R.id.lev_szuk_itv_chb);

        clearEditText(R.id.lev_szuk_elles_sorszamai);
        clearEditText(R.id.lev_szuk_enar);
        clearEditText(R.id.lev_szuk_konstrukcios_tol);
        clearEditText(R.id.lev_szuk_konstrukcios_ig);

        clearTextView(R.id.lev_szuk_utolso_elles_tol);
        clearTextView(R.id.lev_szuk_utolso_elles_ig);
        clearTextView(R.id.lev_szuk_szuletes_tol);
        clearTextView(R.id.lev_szuk_szuletes_ig);

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

    private void clearTextView(int resId) {
        TextView textView = (TextView) findViewById(resId);
        textView.setText("");
    }

    private void clearRadioButton(int resId) {
        RadioButton radioButton = (RadioButton) findViewById(resId);
        radioButton.setChecked(false);
    }

    public void showSzukites() {
        pane.openPane();
    }

    public void pickDate(final View view) {
        final TextView dateEditText = (TextView) view;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        FragmentTransaction ft = getFragmentTransactionWithTag("datePickerDialog");
        dialog = KbrDatePickerDialog.newInstance(new DatePickedListener() {
            @Override
            public void onClear() {
                dateEditText.setText("");
                dismissDialog();
            }

            @Override
            public void onDatePicked(int year, int monthOfYear, int dayOfMonth) {
                dateEditText.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                dismissDialog();
            }
        }, mYear, mMonth, mDay);
        dialog.show(ft, "datePickerDialog");

    }

}
